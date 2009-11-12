package no.tornado.brap.auth;

import no.tornado.brap.common.InvocationRequest;
import no.tornado.brap.common.UsernamePasswordPrincipal;
import no.tornado.brap.exception.AuthenticationFailedException;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Authenticator that uses a JDBC datasource to perform the authentication.
 * This class is intended to be subclassed by clients.
 *
 * Override getAuthSql() method to supply a different query.
 * Override executeAndValidate() to validate the query.
 *
 * TODO: Handle connections properly!!
 */
public class DatabaseUsernamePasswordAuthenticator implements AuthenticationProvider {
    private DataSource dataSource;
    private PreparedStatement cachedAuthStatement;
    
    public DatabaseUsernamePasswordAuthenticator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void authenticate(InvocationRequest invocationRequest) throws AuthenticationFailedException {
        if (invocationRequest.getCredentials() != null && invocationRequest.getCredentials() instanceof UsernamePasswordPrincipal) {
            try {
                UsernamePasswordPrincipal upp = (UsernamePasswordPrincipal) invocationRequest.getCredentials();

                PreparedStatement stmt = getAuthStatement();
                stmt.setString(0, upp.getUsername());
                stmt.setString(1, upp.getPassword());

                executeAndValidate(stmt, invocationRequest);
            } catch (SQLException e) {
                cachedAuthStatement = null;
                e.printStackTrace();
                throw new AuthenticationFailedException(e);
            }
        }
    }

    public void executeAndValidate(PreparedStatement stmt, InvocationRequest invocationRequest) throws SQLException, AuthenticationFailedException {
        if (stmt.executeQuery().next())
            AuthenticationContext.setPrincipal(invocationRequest.getCredentials());
        else
            throw new AuthenticationFailedException("Authentication failed");
    }

    private PreparedStatement getAuthStatement() throws SQLException {
        if (cachedAuthStatement == null)
            cachedAuthStatement = dataSource.getConnection().prepareStatement(getAuthSql());

        return cachedAuthStatement;
    }


    public String getAuthSql() {
        return "SELECT * FROM users WHERE username = ? AND password = ?";
    }

}
