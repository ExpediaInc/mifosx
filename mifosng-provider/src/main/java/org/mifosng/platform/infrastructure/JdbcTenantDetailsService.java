package org.mifosng.platform.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * A JDBC implementation of {@link TenantDetailsService} for loading a tenants details by a <code>tenantIdentifier</code>.
 */
@Service
public class JdbcTenantDetailsService implements TenantDetailsService {

	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JdbcTenantDetailsService(@Qualifier("tenantDataSourceJndi") final DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public MifosPlatformTenant loadTenantById(final String tenantIdentifier) {
		
		try {
			TenantMapper rm = new TenantMapper();
			String sql = "select id, name, schema_name as schemaName, schema_server as schemaServer, schema_server_port as schemaServerPort, " +
					" schema_username as schemaUsername, schema_password as schemaPassword " +
					" from tenants t where t.identifier like ?";
	
			return this.jdbcTemplate.queryForObject(sql, rm, new Object[] {tenantIdentifier});
		} catch (EmptyResultDataAccessException e) {
			throw new InvalidTenantIdentiferException("The tenant identifier: " + tenantIdentifier + " is not valid.");
		}
	}
	
	private static final class TenantMapper implements RowMapper<MifosPlatformTenant> {

		@Override
		public MifosPlatformTenant mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String name = rs.getString("name");
			String schemaName = rs.getString("schemaName");
			String schemaServer = rs.getString("schemaServer");
			String schemaServerPort = rs.getString("schemaServerPort");
			String schemaUsername = rs.getString("schemaUsername");
			String schemaPassword = rs.getString("schemaPassword");
			
			return new MifosPlatformTenant(id, name, schemaName, schemaServer, schemaServerPort, schemaUsername, schemaPassword);
		}
	}
}