package de.jonahd345.extendedeconomy.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * A custom log filter that filters out specific log events for log4j.
 * This filter is designed to filter out INFO level logs from Hikari.
 */
public class LogFilter extends AbstractFilter
{
    /**
     * Registers this filter on the root logger.
     */
    public static void registerFilter()
    {
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new LogFilter());
    }

    @Override
    public Result filter(LogEvent event)
    {
        if(event == null)
        {
            return Result.NEUTRAL;
        }
        // Filter Hikari INFO logs
        if(event.getLoggerName().contains("Hikari") && event.getLevel() == Level.INFO)
        {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t)
    {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params)
    {
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t)
    {
        return Result.NEUTRAL;
    }
}
