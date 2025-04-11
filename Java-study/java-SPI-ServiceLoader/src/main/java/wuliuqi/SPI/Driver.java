package dongfeng.SPI;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author dongfeng
 * @date 2023/4/3 10:58
 */
public interface Driver {
    Connection connect(String url, java.util.Properties info)
            throws SQLException;
}
