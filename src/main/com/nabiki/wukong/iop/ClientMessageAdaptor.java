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

package com.nabiki.wukong.iop;

import com.nabiki.ctp4j.jni.struct.*;
import com.nabiki.wukong.ctp4j.jni.struct.CThostFtdcActionUuidField;
import com.nabiki.wukong.ctp4j.jni.struct.CThostFtdcCandleField;
import com.nabiki.wukong.ctp4j.jni.struct.CThostFtdcOrderUuidField;

import java.util.UUID;

public abstract class ClientMessageAdaptor {
    public void rspReqLogin(CThostFtdcRspUserLoginField login, UUID requestID,
                            UUID responseID, int count, int total) {
    }

    public void rspReqOrderInsert(CThostFtdcOrderUuidField order, UUID requestID,
                                  UUID responseID, int count, int total) {
    }

    public void rspReqOrderAction(CThostFtdcActionUuidField order, UUID requestID,
                                  UUID responseID, int count, int total) {
    }

    public void rspQryAccount(CThostFtdcTradingAccountField account, UUID requestID,
                              UUID responseID,int count, int total) {
    }

    public void rspQryOrder(CThostFtdcOrderField rtnOrder, UUID requestID,
                            UUID responseID,int count, int total) {
    }

    public void rspQryPosition(CThostFtdcInvestorPositionField position,
                               UUID requestID, UUID responseID, int count,
                               int total) {
    }

    public void rspQryOrderExec(CThostFtdcRspInfoField rsp, UUID requestID,
                                UUID responseID, int count, int total) {
    }

    public void rspQryActionExec(CThostFtdcRspInfoField rsp, UUID requestID,
                                 UUID responseID, int count, int total) {
    }

    public void rspQryUserExec(CThostFtdcRspInfoField rsp, UUID requestID,
                                UUID responseID, int count, int total) {
    }

    public void rspSubscribeMarketData(CThostFtdcSpecificInstrumentField subscribe,
                                       UUID requestID, UUID responseID, int count,
                                       int total) {
    }

    public void rspDepthMarketData(CThostFtdcDepthMarketDataField depth) {
    }

    public void rspCandle(CThostFtdcCandleField candle) {
    }
}
