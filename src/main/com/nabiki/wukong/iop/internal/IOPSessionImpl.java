/*
 * Copyright (c) 2020 Hongbao Chen <chenhongbao@outlook.com>
 *
 * Licensed under the  GNU Affero General Public License v3.0 and you may not use
 * this file except in compliance with the  License. You may obtain a copy of the
 * License at
 *
 *                    https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Permission is hereby  granted, free of charge, to any  person obtaining a copy
 * of this software and associated  documentation files (the "Software"), to deal
 * in the Software  without restriction, including without  limitation the rights
 * to  use, copy,  modify, merge,  publish, distribute,  sublicense, and/or  sell
 * copies  of  the Software,  and  to  permit persons  to  whom  the Software  is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE  IS PROVIDED "AS  IS", WITHOUT WARRANTY  OF ANY KIND,  EXPRESS OR
 * IMPLIED,  INCLUDING BUT  NOT  LIMITED TO  THE  WARRANTIES OF  MERCHANTABILITY,
 * FITNESS FOR  A PARTICULAR PURPOSE AND  NONINFRINGEMENT. IN NO EVENT  SHALL THE
 * AUTHORS  OR COPYRIGHT  HOLDERS  BE  LIABLE FOR  ANY  CLAIM,  DAMAGES OR  OTHER
 * LIABILITY, WHETHER IN AN ACTION OF  CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE  OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nabiki.wukong.iop.internal;

import com.nabiki.wukong.iop.IOPSession;
import com.nabiki.wukong.iop.frame.Body;
import com.nabiki.wukong.iop.frame.Frame;
import com.nabiki.wukong.iop.frame.FrameType;
import com.nabiki.wukong.iop.frame.OP;
import org.apache.mina.core.session.IoSession;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IOPSessionImpl implements IOPSession {
    private IoSession session;

    private final Map<String, Object> attributes = new HashMap<>();

    IOPSessionImpl() {}

    void wrap(IoSession ioSession) {
        synchronized (this) {
            this.session = ioSession;
        }
    }

    @Override
    public void close() {
        synchronized (this) {
            if (this.session != null && this.session.isConnected()) {
                var future = this.session.closeNow();
                future.awaitUninterruptibly();
            }
        }
    }

    private void send(Body message, int type) {
        if (message == null)
            throw new NullPointerException("message null");
        synchronized (this) {
            if (this.session == null)
                throw new IllegalStateException("session null");
            // Get body bytes.
            var bytes = OP.toJson(message).getBytes(StandardCharsets.UTF_8);
            // Construct frame.
            var req = new Frame();
            req.Type = type;
            req.Length = bytes.length;
            req.Body = bytes;
            // Send frame.
            this.session.write(req);
        }
    }

    @Override
    public void sendRequest(Body message) {
        send(message, FrameType.REQUEST);
    }

    @Override
    public void sendResponse(Body message) {
        send(message, FrameType.RESPONSE);
    }

    @Override
    public void sendHeartbeat(UUID heartbeatID) {
        var body = new Body();
        body.RequestID = heartbeatID;
        send(body, FrameType.HEARTBEAT);
    }

    @Override
    public void setAttribute(String key, Object attribute) {
        synchronized (this.attributes) {
            this.attributes.put(key, attribute);
        }
    }

    @Override
    public void removeAttribute(String key) {
        synchronized (this.attributes) {
            this.attributes.remove(key);
        }
    }

    @Override
    public Object getAttribute(String key) {
        synchronized (this.attributes) {
            return this.attributes.get(key);
        }
    }
}
