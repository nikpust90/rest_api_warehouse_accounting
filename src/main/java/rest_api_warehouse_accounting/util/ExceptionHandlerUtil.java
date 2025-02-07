package rest_api_warehouse_accounting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Supplier;

public class ExceptionHandlerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerUtil.class);

    public static <T> T handleDatabaseOperation(Supplier<T> operation, String errorMessage) {
        try {
            return operation.get();
        } catch (Exception e) {
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }
    }
}
