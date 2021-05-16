package org.infodavid.util.logging;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.FileSize;

/**
 * The Class LoggingUtils.
 */
public final class LoggingUtils {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

    /** The encoder pattern. */
    public static final String ENCODER_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    /** The Constant ROLLING_SUFFIX. */
    public static final String ROLLING_SUFFIX = "-%d{yyyy-MM-dd}.%i.log.gz";

    /** The singleton. */
    private static WeakReference<LoggingUtils> instance = null;

    /**
     * returns the singleton.
     * @return the singleton
     */
    public static synchronized LoggingUtils getInstance() {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new LoggingUtils());
        }

        return instance.get();
    }

    /**
     * Instantiates a new util.
     */
    private LoggingUtils() {
        super();
    }

    /**
     * Close the appender.
     * @param appender the appender
     */
    public void close(final Appender<ILoggingEvent> appender) {
        LOGGER.debug("Closing appender: {}", appender.getName());

        // Appender can be reused if state machine is halted anormally and started again
        if (Thread.currentThread().getName().equalsIgnoreCase("Finalizer")) {
            LOGGER.debug("Resource will not be closed by the finalizer thread: {}", appender.getName());
        }
        else {
            final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
            final ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);

            root.detachAppender(appender);
            appender.stop();
        }

        LOGGER.debug("Appender: {} is now closed", appender.getName());
    }

    /**
     * Close the logger.
     * @param logger the logger
     */
    @SuppressWarnings("unchecked")
    public void close(final Logger logger) {
        LOGGER.debug("Closing logger: {}", logger.getName());

        try {
            final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
            final java.util.Map<String,ch.qos.logback.classic.Logger> loggerCache = (Map<String,ch.qos.logback.classic.Logger>)FieldUtils.readField(context, "loggerCache", true);

            if (loggerCache.get(logger.getName()) == logger) {
                loggerCache.remove(logger.getName());
            }
        }
        catch (final IllegalAccessException e) {
            LOGGER.error("An error occured while removing logger from logback cache", e);
        }

        LOGGER.debug("Logger: {} is now closed", logger.getName());
    }

    /**
     * Gets the appender.
     * @param identifier the identifier
     * @return the appender
     * @throws IllegalAccessException the illegal access exception
     */
    @SuppressWarnings("unchecked")
    public Appender<ILoggingEvent> getAppender(final String identifier) throws IllegalAccessException {
        final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        final ch.qos.logback.classic.Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        final AppenderAttachable<ILoggingEvent> aai = (AppenderAttachable<ILoggingEvent>)FieldUtils.readField(root, "aai", true);

        return aai.getAppender(identifier);
    }

    /**
     * Creates the appender.
     * @param file the file
     * @param attachToRoot the attach to root
     * @return the file appender
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public FileAppender<ILoggingEvent> newFileAppender(final Path file, final boolean attachToRoot) throws IOException {
        final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        final FileAppender<ILoggingEvent> result = new FileAppender<>();

        Files.deleteIfExists(file);
        Files.createDirectories(file.getParent());

        result.setName(file.getFileName().toString());
        result.setFile(file.toString());
        result.setContext(context);
        result.setEncoder(newPatternLayoutEncoder());
        result.start();

        if (attachToRoot) {
            context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(result);
        }

        return result;
    }

    /**
     * New pattern layout encoder.
     * @return the pattern layout encoder
     */
    public PatternLayoutEncoder newPatternLayoutEncoder() {
        final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder result = new PatternLayoutEncoder();

        result.setContext(context);
        result.setPattern(ENCODER_PATTERN);
        result.setCharset(StandardCharsets.UTF_8);
        result.start();

        return result;
    }

    /**
     * New rolling policy.
     * @param archivesDirectory the archives directory
     * @param archiveBaseName the archive base name
     * @param maxFileSize the max file size
     * @param maxHistory the max history
     * @param appender the appender
     * @return the size and time based rolling policy
     */
    public SizeAndTimeBasedRollingPolicy<ILoggingEvent> newRollingPolicy(final Path archivesDirectory, final String archiveBaseName, final FileSize maxFileSize, final int maxHistory, final FileAppender<ILoggingEvent> appender) {
        final LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        final SizeAndTimeBasedRollingPolicy<ILoggingEvent> result = new SizeAndTimeBasedRollingPolicy<>();

        result.setContext(context);
        result.setFileNamePattern(archivesDirectory.resolve(archiveBaseName + ROLLING_SUFFIX).toAbsolutePath().toString());
        result.setMaxFileSize(maxFileSize);
        result.setMaxHistory(maxHistory);
        result.setParent(appender);
        result.start();

        return result;
    }
}
