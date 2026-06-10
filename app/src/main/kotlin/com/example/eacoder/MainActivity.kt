package com.example.eacoder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fastEMAInput = findViewById<EditText>(R.id.fastEMA)
        val slowEMAInput = findViewById<EditText>(R.id.slowEMA)
        val rsiInput = findViewById<EditText>(R.id.rsiPeriod)
        val riskInput = findViewById<EditText>(R.id.baseRisk)
        val stopLossInput = findViewById<EditText>(R.id.stopLoss)
        val takeProfitInput = findViewById<EditText>(R.id.takeProfit)
        val mtTypeSpinner = findViewById<Spinner>(R.id.mtType)
        val generateButton = findViewById<Button>(R.id.generateEAButton)

        generateButton.setOnClickListener {
            try {
                val fastEMA = fastEMAInput.text.toString().toIntOrNull()
                    ?: throw NumberFormatException("Fast EMA")
                val slowEMA = slowEMAInput.text.toString().toIntOrNull()
                    ?: throw NumberFormatException("Slow EMA")
                val rsi = rsiInput.text.toString().toIntOrNull()
                    ?: throw NumberFormatException("RSI Period")
                val risk = riskInput.text.toString().toDoubleOrNull()
                    ?: throw NumberFormatException("Risk")
                val stopLoss = stopLossInput.text.toString().toIntOrNull()
                    ?: throw NumberFormatException("Stop Loss")
                val takeProfit = takeProfitInput.text.toString().toIntOrNull()
                    ?: throw NumberFormatException("Take Profit")
                val mtType = mtTypeSpinner.selectedItem.toString()

                val eaCode = generateEA(fastEMA, slowEMA, rsi, risk, stopLoss, takeProfit, mtType)
                saveEAFile(eaCode, mtType)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateEA(
        fastEMA: Int,
        slowEMA: Int,
        rsi: Int,
        risk: Double,
        stopLoss: Int,
        takeProfit: Int,
        mtType: String
    ): String {
        return if (mtType == "MT4") {
            generateMT4EA(fastEMA, slowEMA, rsi, risk, stopLoss, takeProfit)
        } else {
            generateMT5EA(fastEMA, slowEMA, rsi, risk, stopLoss, takeProfit)
        }
    }

    private fun generateMT4EA(
        fastEMA: Int,
        slowEMA: Int,
        rsi: Int,
        risk: Double,
        stopLoss: Int,
        takeProfit: Int
    ): String {
        return """
//+------------------------------------------------------------------+
//| EA Coder - Auto-Generated MT4 Expert Advisor                    |
//| Strategy: EMA Crossover + RSI Filter                            |
//| Generated: ${java.time.LocalDateTime.now()}                      |
//+------------------------------------------------------------------+
#property strict
#include <Trade\Trade.mqh>

CTrade trade;

//--- Input parameters
input int FastEMA = $fastEMA;
input int SlowEMA = $slowEMA;
input int RSI_Period = $rsi;
input double BaseRiskPercent = $risk;
input int StopLossPoints = $stopLoss;
input int TakeProfitPoints = $takeProfit;
input int MagicNumber = 12345;
input double LotSize = 0.1;
input bool UseMoneyManagement = true;
input int MaxSpread = 50;

//--- Global variables
double FastEMAValue, SlowEMAValue, RSIValue;
int LastBarIndex = -1;
ulong LastTicket = 0;

//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
int OnInit() {
    Print("EA initialized with parameters:");
    Print("Fast EMA: ", FastEMA);
    Print("Slow EMA: ", SlowEMA);
    Print("RSI Period: ", RSI_Period);
    Print("Risk: ", BaseRiskPercent, "%");
    Print("Stop Loss: ", StopLossPoints, " points");
    Print("Take Profit: ", TakeProfitPoints, " points");
    return(INIT_SUCCEEDED);
}

//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason) {
    Print("EA deinitialized. Reason: ", reason);
}

//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick() {
    // Prevent multiple trades on same bar
    if(LastBarIndex == iBarShift(Symbol(), Period(), TimeCurrent())) {
        return;
    }
    LastBarIndex = iBarShift(Symbol(), Period(), TimeCurrent());

    // Check market conditions
    if(!IsMarketOpen()) {
        return;
    }

    // Calculate indicators
    FastEMAValue = iMA(Symbol(), Period(), FastEMA, 0, MODE_EMA, PRICE_CLOSE, 1);
    SlowEMAValue = iMA(Symbol(), Period(), SlowEMA, 0, MODE_EMA, PRICE_CLOSE, 1);
    RSIValue = iRSI(Symbol(), Period(), RSI_Period, PRICE_CLOSE, 1);

    // Get current bid/ask
    double CurrentBid = SymbolInfoDouble(Symbol(), SYMBOL_BID);
    double CurrentAsk = SymbolInfoDouble(Symbol(), SYMBOL_ASK);
    double Spread = (CurrentAsk - CurrentBid) / Point();

    // Check spread
    if(Spread > MaxSpread) {
        Print("Spread too high: ", Spread);
        return;
    }

    // Close existing positions if conditions reverse
    ClosePositionsIfReverse();

    // BUY Signal: Fast EMA > Slow EMA AND RSI not overbought
    if(FastEMAValue > SlowEMAValue && RSIValue < 70) {
        if(!HasBuyPosition()) {
            OpenBuyPosition(CurrentAsk, StopLossPoints, TakeProfitPoints);
        }
    }
    // SELL Signal: Fast EMA < Slow EMA AND RSI not oversold
    else if(FastEMAValue < SlowEMAValue && RSIValue > 30) {
        if(!HasSellPosition()) {
            OpenSellPosition(CurrentBid, StopLossPoints, TakeProfitPoints);
        }
    }

    UpdateTradeComment();
}

//+------------------------------------------------------------------+
//| Open BUY position                                                |
//+------------------------------------------------------------------+
void OpenBuyPosition(double Ask, int SLPoints, int TPPoints) {
    double StopLoss = Ask - (SLPoints * Point());
    double TakeProfit = Ask + (TPPoints * Point());
    double Lot = UseMoneyManagement ? CalculateLotSize(SLPoints) : LotSize;

    if(Lot <= 0) {
        Print("Invalid lot size calculated: ", Lot);
        return;
    }

    if(trade.Buy(Lot, Symbol(), Ask, StopLoss, TakeProfit, "EA Buy Signal")) {
        LastTicket = trade.ResultOrder();
        Print("BUY position opened. Ticket: ", LastTicket);
    } else {
        Print("BUY failed. Error: ", trade.ResultRetcodeDescription());
    }
}

//+------------------------------------------------------------------+
//| Open SELL position                                               |
//+------------------------------------------------------------------+
void OpenSellPosition(double Bid, int SLPoints, int TPPoints) {
    double StopLoss = Bid + (SLPoints * Point());
    double TakeProfit = Bid - (TPPoints * Point());
    double Lot = UseMoneyManagement ? CalculateLotSize(SLPoints) : LotSize;

    if(Lot <= 0) {
        Print("Invalid lot size calculated: ", Lot);
        return;
    }

    if(trade.Sell(Lot, Symbol(), Bid, StopLoss, TakeProfit, "EA Sell Signal")) {
        LastTicket = trade.ResultOrder();
        Print("SELL position opened. Ticket: ", LastTicket);
    } else {
        Print("SELL failed. Error: ", trade.ResultRetcodeDescription());
    }
}

//+------------------------------------------------------------------+
//| Close positions if signal reverses                               |
//+------------------------------------------------------------------+
void ClosePositionsIfReverse() {
    for(int i = PositionsTotal() - 1; i >= 0; i--) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber) {
                
                ENUM_POSITION_TYPE PosType = (ENUM_POSITION_TYPE)PositionGetInteger(POSITION_TYPE);
                
                if(PosType == POSITION_TYPE_BUY && FastEMAValue < SlowEMAValue) {
                    trade.PositionClose(PositionGetTicket(i));
                    Print("Closed BUY position due to signal reversal");
                } 
                else if(PosType == POSITION_TYPE_SELL && FastEMAValue > SlowEMAValue) {
                    trade.PositionClose(PositionGetTicket(i));
                    Print("Closed SELL position due to signal reversal");
                }
            }
        }
    }
}

//+------------------------------------------------------------------+
//| Check if has BUY position                                        |
//+------------------------------------------------------------------+
bool HasBuyPosition() {
    for(int i = 0; i < PositionsTotal(); i++) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber &&
               PositionGetInteger(POSITION_TYPE) == POSITION_TYPE_BUY) {
                return true;
            }
        }
    }
    return false;
}

//+------------------------------------------------------------------+
//| Check if has SELL position                                       |
//+------------------------------------------------------------------+
bool HasSellPosition() {
    for(int i = 0; i < PositionsTotal(); i++) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber &&
               PositionGetInteger(POSITION_TYPE) == POSITION_TYPE_SELL) {
                return true;
            }
        }
    }
    return false;
}

//+------------------------------------------------------------------+
//| Calculate lot size based on risk management                      |
//+------------------------------------------------------------------+
double CalculateLotSize(int SLPoints) {
    double AccountBalance = AccountInfoDouble(ACCOUNT_BALANCE);
    double RiskAmount = (AccountBalance * BaseRiskPercent) / 100.0;
    double PipValue = (SymbolInfoDouble(Symbol(), SYMBOL_TRADE_TICK_SIZE) / 
                       SymbolInfoDouble(Symbol(), SYMBOL_POINT));
    double LotValue = RiskAmount / (SLPoints * PipValue);
    double MinLot = SymbolInfoDouble(Symbol(), SYMBOL_VOLUME_MIN);
    double MaxLot = SymbolInfoDouble(Symbol(), SYMBOL_VOLUME_MAX);
    
    LotValue = MathMax(LotValue, MinLot);
    LotValue = MathMin(LotValue, MaxLot);
    
    return LotValue;
}

//+------------------------------------------------------------------+
//| Check if market is open                                          |
//+------------------------------------------------------------------+
bool IsMarketOpen() {
    return (SymbolInfoInteger(Symbol(), SYMBOL_SESSION_DEALS) > 0);
}

//+------------------------------------------------------------------+
//| Update trade comment with current indicators                     |
//+------------------------------------------------------------------+
void UpdateTradeComment() {
    string comment = StringFormat("Fast EMA: %.2f | Slow EMA: %.2f | RSI: %.2f",
                                  FastEMAValue, SlowEMAValue, RSIValue);
    // Update all positions' comments
    for(int i = 0; i < PositionsTotal(); i++) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetInteger(POSITION_MAGIC) == MagicNumber) {
                trade.OrderModify(PositionGetTicket(i), 0, 
                                  PositionGetDouble(POSITION_SL), 
                                  PositionGetDouble(POSITION_TP));
            }
        }
    }
}

//+------------------------------------------------------------------+
//| End of Expert Advisor                                            |
//+------------------------------------------------------------------+
        """.trimIndent()
    }

    private fun generateMT5EA(
        fastEMA: Int,
        slowEMA: Int,
        rsi: Int,
        risk: Double,
        stopLoss: Int,
        takeProfit: Int
    ): String {
        return """
//+------------------------------------------------------------------+
//| EA Coder - Auto-Generated MT5 Expert Advisor                    |
//| Strategy: EMA Crossover + RSI Filter                            |
//| Generated: ${java.time.LocalDateTime.now()}                      |
//+------------------------------------------------------------------+
#property copyright "EA Coder"
#property link      "https://eacoder.example.com"
#property version   "1.0"
#property strict
#property description "MT5 EA with EMA Crossover and RSI Filter"

#include <Trade\Trade.mqh>

CTrade trade;

//--- Input parameters
input int FastEMA = $fastEMA;
input int SlowEMA = $slowEMA;
input int RSI_Period = $rsi;
input double BaseRiskPercent = $risk;
input int StopLossPoints = $stopLoss;
input int TakeProfitPoints = $takeProfit;
input int MagicNumber = 12345;
input double LotSize = 0.1;
input bool UseMoneyManagement = true;
input int MaxSpread = 50;
input bool PrintToChart = true;

//--- Indicators
int FastHandle, SlowHandle, RSIHandle;

//--- Global variables
double FastEMAValue, SlowEMAValue, RSIValue;
datetime LastBarTime = 0;

//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
int OnInit() {
    Print("MT5 EA initialized with parameters:");
    Print("Fast EMA: ", FastEMA);
    Print("Slow EMA: ", SlowEMA);
    Print("RSI Period: ", RSI_Period);
    Print("Risk: ", BaseRiskPercent, "%");
    Print("Stop Loss: ", StopLossPoints, " points");
    Print("Take Profit: ", TakeProfitPoints, " points");
    
    // Create indicator handles
    FastHandle = iMA(Symbol(), Period(), FastEMA, 0, MODE_EMA, PRICE_CLOSE);
    SlowHandle = iMA(Symbol(), Period(), SlowEMA, 0, MODE_EMA, PRICE_CLOSE);
    RSIHandle = iRSI(Symbol(), Period(), RSI_Period, PRICE_CLOSE);
    
    if(FastHandle == INVALID_HANDLE || SlowHandle == INVALID_HANDLE || RSIHandle == INVALID_HANDLE) {
        Print("Error creating indicators!");
        return INIT_FAILED;
    }
    
    return INIT_SUCCEEDED;
}

//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason) {
    Print("EA deinitialized. Reason: ", reason);
    
    // Release indicator handles
    ReleasedHandle(FastHandle);
    ReleasedHandle(SlowHandle);
    ReleasedHandle(RSIHandle);
}

//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick() {
    // Prevent multiple trades on same bar
    if(TimeCurrent() == LastBarTime) {
        return;
    }
    LastBarTime = TimeCurrent();

    // Copy indicator values
    if(!GetIndicatorValues()) {
        return;
    }

    // Get current bid/ask
    double CurrentBid = SymbolInfoDouble(Symbol(), SYMBOL_BID);
    double CurrentAsk = SymbolInfoDouble(Symbol(), SYMBOL_ASK);
    double Spread = (CurrentAsk - CurrentBid) / SymbolInfoDouble(Symbol(), SYMBOL_POINT);

    // Check spread
    if(Spread > MaxSpread) {
        if(PrintToChart) Print("Spread too high: ", Spread);
        return;
    }

    // Close existing positions if conditions reverse
    ClosePositionsIfReverse();

    // BUY Signal: Fast EMA > Slow EMA AND RSI not overbought
    if(FastEMAValue > SlowEMAValue && RSIValue < 70) {
        if(!HasBuyPosition()) {
            OpenBuyPosition(CurrentAsk, StopLossPoints, TakeProfitPoints);
        }
    }
    // SELL Signal: Fast EMA < Slow EMA AND RSI not oversold
    else if(FastEMAValue < SlowEMAValue && RSIValue > 30) {
        if(!HasSellPosition()) {
            OpenSellPosition(CurrentBid, StopLossPoints, TakeProfitPoints);
        }
    }

    UpdateTradeComment();
}

//+------------------------------------------------------------------+
//| Get indicator values                                             |
//+------------------------------------------------------------------+
bool GetIndicatorValues() {
    double FastBuf[], SlowBuf[], RSIBuf[];
    
    ArraySetAsSeries(FastBuf, true);
    ArraySetAsSeries(SlowBuf, true);
    ArraySetAsSeries(RSIBuf, true);
    
    // Copy indicator values
    if(CopyBuffer(FastHandle, 0, 0, 2, FastBuf) <= 0) return false;
    if(CopyBuffer(SlowHandle, 0, 0, 2, SlowBuf) <= 0) return false;
    if(CopyBuffer(RSIHandle, 0, 0, 2, RSIBuf) <= 0) return false;
    
    FastEMAValue = FastBuf[0];
    SlowEMAValue = SlowBuf[0];
    RSIValue = RSIBuf[0];
    
    return true;
}

//+------------------------------------------------------------------+
//| Open BUY position                                                |
//+------------------------------------------------------------------+
void OpenBuyPosition(double Ask, int SLPoints, int TPPoints) {
    double StopLoss = Ask - (SLPoints * SymbolInfoDouble(Symbol(), SYMBOL_POINT));
    double TakeProfit = Ask + (TPPoints * SymbolInfoDouble(Symbol(), SYMBOL_POINT));
    double Lot = UseMoneyManagement ? CalculateLotSize(SLPoints) : LotSize;

    if(Lot <= 0) {
        Print("Invalid lot size calculated: ", Lot);
        return;
    }

    if(trade.Buy(Lot, Symbol(), Ask, StopLoss, TakeProfit, "EA Buy Signal")) {
        if(PrintToChart) Print("BUY position opened. Ticket: ", trade.ResultOrder());
    } else {
        if(PrintToChart) Print("BUY failed. Error: ", trade.ResultRetcodeDescription());
    }
}

//+------------------------------------------------------------------+
//| Open SELL position                                               |
//+------------------------------------------------------------------+
void OpenSellPosition(double Bid, int SLPoints, int TPPoints) {
    double StopLoss = Bid + (SLPoints * SymbolInfoDouble(Symbol(), SYMBOL_POINT));
    double TakeProfit = Bid - (TPPoints * SymbolInfoDouble(Symbol(), SYMBOL_POINT));
    double Lot = UseMoneyManagement ? CalculateLotSize(SLPoints) : LotSize;

    if(Lot <= 0) {
        Print("Invalid lot size calculated: ", Lot);
        return;
    }

    if(trade.Sell(Lot, Symbol(), Bid, StopLoss, TakeProfit, "EA Sell Signal")) {
        if(PrintToChart) Print("SELL position opened. Ticket: ", trade.ResultOrder());
    } else {
        if(PrintToChart) Print("SELL failed. Error: ", trade.ResultRetcodeDescription());
    }
}

//+------------------------------------------------------------------+
//| Close positions if signal reverses                               |
//+------------------------------------------------------------------+
void ClosePositionsIfReverse() {
    for(int i = PositionsTotal() - 1; i >= 0; i--) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber) {
                
                ENUM_POSITION_TYPE PosType = (ENUM_POSITION_TYPE)PositionGetInteger(POSITION_TYPE);
                
                if(PosType == POSITION_TYPE_BUY && FastEMAValue < SlowEMAValue) {
                    trade.PositionClose(PositionGetTicket(i));
                    if(PrintToChart) Print("Closed BUY position due to signal reversal");
                } 
                else if(PosType == POSITION_TYPE_SELL && FastEMAValue > SlowEMAValue) {
                    trade.PositionClose(PositionGetTicket(i));
                    if(PrintToChart) Print("Closed SELL position due to signal reversal");
                }
            }
        }
    }
}

//+------------------------------------------------------------------+
//| Check if has BUY position                                        |
//+------------------------------------------------------------------+
bool HasBuyPosition() {
    for(int i = 0; i < PositionsTotal(); i++) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber &&
               PositionGetInteger(POSITION_TYPE) == POSITION_TYPE_BUY) {
                return true;
            }
        }
    }
    return false;
}

//+------------------------------------------------------------------+
//| Check if has SELL position                                       |
//+------------------------------------------------------------------+
bool HasSellPosition() {
    for(int i = 0; i < PositionsTotal(); i++) {
        if(PositionSelectByTicket(PositionGetTicket(i))) {
            if(PositionGetString(POSITION_SYMBOL) == Symbol() && 
               PositionGetInteger(POSITION_MAGIC) == MagicNumber &&
               PositionGetInteger(POSITION_TYPE) == POSITION_TYPE_SELL) {
                return true;
            }
        }
    }
    return false;
}

//+------------------------------------------------------------------+
//| Calculate lot size based on risk management                      |
//+------------------------------------------------------------------+
double CalculateLotSize(int SLPoints) {
    double AccountBalance = AccountInfoDouble(ACCOUNT_BALANCE);
    double RiskAmount = (AccountBalance * BaseRiskPercent) / 100.0;
    double TickValue = SymbolInfoDouble(Symbol(), SYMBOL_TRADE_TICK_VALUE);
    double Point = SymbolInfoDouble(Symbol(), SYMBOL_POINT);
    double LotValue = RiskAmount / (SLPoints * Point * TickValue);
    double MinLot = SymbolInfoDouble(Symbol(), SYMBOL_VOLUME_MIN);
    double MaxLot = SymbolInfoDouble(Symbol(), SYMBOL_VOLUME_MAX);
    
    LotValue = MathMax(LotValue, MinLot);
    LotValue = MathMin(LotValue, MaxLot);
    
    return LotValue;
}

//+------------------------------------------------------------------+
//| Release indicator handle                                         |
//+------------------------------------------------------------------+
void ReleasedHandle(int handle) {
    if(handle != INVALID_HANDLE) {
        IndicatorRelease(handle);
    }
}

//+------------------------------------------------------------------+
//| Update trade comment with current indicators                     |
//+------------------------------------------------------------------+
void UpdateTradeComment() {
    string comment = StringFormat("Fast EMA: %.2f | Slow EMA: %.2f | RSI: %.2f",
                                  FastEMAValue, SlowEMAValue, RSIValue);
    if(PrintToChart) {
        Comment(comment);
    }
}

//+------------------------------------------------------------------+
//| End of Expert Advisor                                            |
//+------------------------------------------------------------------+
        """.trimIndent()
    }

    private fun saveEAFile(content: String, mtType: String) {
        try {
            val extension = if (mtType == "MT4") "mq4" else "mq5"
            val fileName = "EA_${System.currentTimeMillis()}.$extension"
            val file = File(getExternalFilesDir(null), fileName)
            file.writeText(content)
            Toast.makeText(this, "EA saved: ${file.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
