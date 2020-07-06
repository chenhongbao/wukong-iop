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

import com.nabiki.ctp4j.jni.struct.*;
import com.nabiki.wukong.ctp4j.jni.struct.CThostFtdcOrderUUID;
import com.nabiki.wukong.iop.ClientMessageAdaptor;
import com.nabiki.wukong.iop.ServerMessageAdaptor;
import com.nabiki.wukong.iop.SessionAdaptor;
import com.nabiki.wukong.iop.SessionEvent;
import com.nabiki.wukong.iop.frame.Body;
import com.nabiki.wukong.iop.frame.Frame;
import com.nabiki.wukong.iop.frame.FrameType;
import com.nabiki.wukong.iop.frame.OP;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FrameHandler implements IoHandler {
    /*
    Default adaptors. The adaptors can be override by setter.
     */
    private static class DefaultSessionAdaptor implements SessionAdaptor {
        @Override
        public void event(SessionEvent event, Object eventObject) {
        }
    }

    private static class DefaultClientMessageAdaptor extends ClientMessageAdaptor {
    }

    private static class DefaultServerMessageAdaptor extends ServerMessageAdaptor {
    }

    // Internal session that is re-wrapped at each callback and passed to adaptor.
    private final IOPSessionImpl session = new IOPSessionImpl();

    private SessionAdaptor sessionAdaptor = new DefaultSessionAdaptor();
    private ClientMessageAdaptor clientAdaptor = new DefaultClientMessageAdaptor();
    private ServerMessageAdaptor serverAdaptor = new DefaultServerMessageAdaptor();

    void setMessageAdaptor(ServerMessageAdaptor adaptor) {
        this.serverAdaptor = adaptor;
    }

    void setMessageAdaptor(ClientMessageAdaptor adaptor) {
        this.clientAdaptor = adaptor;
    }

    void setSessionAdaptor(SessionAdaptor adaptor) {
        this.sessionAdaptor = adaptor;
    }

    IOPSessionImpl getIOPSession() {
        return this.session;
    }

    private void handleRequest(Body body) throws IOException {
        switch (body.Type) {
            case QRY_POSITION:
                var qryPosition = OP.fromJson(body.Json,
                        CThostFtdcQryInvestorPositionField.class);
                this.serverAdaptor.qryPosition(this.session, qryPosition,
                        body.RequestID, body.CurrentCount, body.TotalCount);
                break;
            case QRY_ORDER:
                var qryOrder = OP.fromJson(body.Json, CThostFtdcOrderUUID.class);
                this.serverAdaptor.qryOrder(this.session, qryOrder, body.RequestID,
                        body.CurrentCount, body.TotalCount);
                break;
            case QRY_ACCOUNT:
                var qryAccount = OP.fromJson(body.Json,
                        CThostFtdcQryTradingAccountField.class);
                this.serverAdaptor.qryAccount(this.session, qryAccount,
                        body.RequestID, body.CurrentCount, body.TotalCount);
                break;
            case REQ_ORDER_ACTION:
                var reqAction = OP.fromJson(body.Json,
                        CThostFtdcInputOrderActionField.class);
                this.serverAdaptor.reqOrderAction(this.session, reqAction,
                        body.RequestID, body.CurrentCount, body.TotalCount);
                break;
            case REQ_ORDER_INSERT:
                var reqOrder = OP.fromJson(body.Json,
                        CThostFtdcInputOrderField.class);
                this.serverAdaptor.reqOrderInsert(this.session, reqOrder,
                        body.RequestID, body.CurrentCount, body.TotalCount);
                break;
            default:
                throw new IllegalStateException(
                        "unmatched message type, need request");
        }
    }

    private void handleResponse(Body body) throws IOException {
        switch (body.Type) {
            case RSP_QRY_ORDER:
                var rspOrder = OP.fromJson(body.Json, CThostFtdcOrderField.class);
                this.clientAdaptor.rspQryOrder(rspOrder, body.RequestID,
                        body.ResponseID, body.CurrentCount, body.TotalCount);
                break;
            case RSP_QRY_POSITION:
                var rspPosition = OP.fromJson(body.Json,
                        CThostFtdcInvestorPositionField.class);
                this.clientAdaptor.rspQryPosition(rspPosition, body.RequestID,
                        body.ResponseID, body.CurrentCount, body.TotalCount);
                break;
            case RSP_QRY_ACCOUNT:
                var rspAccount = OP.fromJson(body.Json,
                        CThostFtdcTradingAccountField.class);
                this.clientAdaptor.rspQryAccount(rspAccount, body.RequestID,
                        body.ResponseID, body.CurrentCount, body.TotalCount);
                break;
            case RSP_REQ_ORDER_ACTION:
                var rspAction = OP.fromJson(body.Json, CThostFtdcOrderUUID.class);
                this.clientAdaptor.rspReqOrderAction(rspAction, body.RequestID,
                        body.ResponseID, body.CurrentCount, body.TotalCount);
                break;
            case RSP_REQ_ORDER_INSERT:
                var rspInsert = OP.fromJson(body.Json, CThostFtdcOrderUUID.class);
                this.clientAdaptor.rspReqOrderInsert(rspInsert, body.RequestID,
                        body.ResponseID, body.CurrentCount, body.TotalCount);
                break;
            default:
                throw new IllegalStateException(
                        "unmatched message type, need response");
        }
    }

    private void sendHeartbeat(Body body) throws IOException {
        this.session.sendHeartbeat(body.RequestID);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        this.sessionAdaptor.event(SessionEvent.CREATED, null);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        this.sessionAdaptor.event(SessionEvent.OPENED, null);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        this.sessionAdaptor.event(SessionEvent.CLOSED, null);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        this.sessionAdaptor.event(SessionEvent.IDLE, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        this.sessionAdaptor.event(SessionEvent.ERROR, cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (!(message instanceof Frame))
            throw new IllegalStateException("message is not frame");
        this.session.wrap(session);
        var frame = (Frame) message;
        var body = OP.fromJson(new String(frame.Body, StandardCharsets.UTF_8),
                Body.class);
        switch (frame.Type) {
            case FrameType.REQUEST:
                handleRequest(body);
                break;
            case FrameType.RESPONSE:
                handleResponse(body);
                break;
            case FrameType.HEARTBEAT:
                // If it is server, send back heartbeat.
                if (this.serverAdaptor != null)
                    sendHeartbeat(body);
                break;
            default:
                throw new IllegalStateException("unknown frame type");
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // nothing.
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        this.sessionAdaptor.event(SessionEvent.INPUT_CLOSED, null);
    }

    @Override
    public void event(IoSession session, FilterEvent event) throws Exception {
        // nothing.
    }
}
