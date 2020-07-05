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

package com.nabiki.wukong.iop.codec;

public class FrameType {
    public static final int HEARTBEAT = 0;
    public static final int POST_LOGIN = 1;
    public static final int POST_LOGOUT = 2;
    public static final int POST_ORDER = 3;
    public static final int POST_ACTION = 4;
    public static final int GET_ORDER = 5;
    public static final int GET_ACCOUNT = 6;
    public static final int GET_POSITION = 7;
    public static final int GET_POSITION_DETAIL = 8;
    public static final int RSP_POST_LOGIN = 101;
    public static final int RSP_POST_LOGOUT = 102;
    public static final int RSP_POST_ORDER = 103;
    public static final int RSP_POST_ACTION = 104;
    public static final int RSP_GET_ORDER = 105;
    public static final int RSP_GET_ACCOUNT = 106;
    public static final int RSP_GET_POSITION = 107;
    public static final int RSP_GET_POSITION_DETAIL = 108;
}
