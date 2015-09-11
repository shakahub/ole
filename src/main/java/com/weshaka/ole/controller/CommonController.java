package com.weshaka.ole.controller;

import com.weshaka.ole.annotations.LoggingDebug;
import com.weshaka.ole.annotations.LoggingInfo;
import com.weshaka.ole.funcinf.LoggingPrinter;

public abstract class CommonController {
    @LoggingInfo
    LoggingPrinter info;

    @LoggingDebug
    LoggingPrinter debug;
}
