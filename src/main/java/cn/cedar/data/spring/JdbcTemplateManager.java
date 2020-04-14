package cn.cedar.data.spring;

import cn.cedar.data.JdbcManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cedar12 413338772@qq.com
 */
public class JdbcTemplateManager extends JdbcManager {

    private static final Log log = LogFactory.getLog(JdbcTemplateManager.class);
    protected static boolean displaySql = false;
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateManager(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    private static void displaySql(String sql) {
        if (displaySql) {
            log.info(sql);
        }
    }


    @Override
    public List<Map<String, Object>> excuteQuery(String sql, Object... params) {
        displaySql(sql);
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public int excute(String sql, Object... params) {
        displaySql(sql);
        return jdbcTemplate.update(sql);
    }

    @Override
    public int excuteGetGeneratedKey(final String sql, Object... params) {
        displaySql(sql);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rc = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(java.sql.Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                return ps;
            }
        }, keyHolder);
        if (rc > 0) {
            return keyHolder.getKey().intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Map<String, Object> excuteQueryOne(String sql, Object... params) {
        displaySql(sql);
        Map<String, Object> map=new HashMap<>();
        try {
            map = jdbcTemplate.queryForMap(sql);
        }catch(EmptyResultDataAccessException e){
            log.error(e.getMessage());
        }catch(DataAccessException e){
            log.error(e.getMessage());
        }
        return map;
    }

    @Override
    public long excuteQueryCount(String sql, Object... params) {
        displaySql(sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
