package testsupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import BD.ConnexionMySQL;

/**
 * Support JDBC pour les tests BD
 */
public final class JdbcTestSupport {

    private JdbcTestSupport() {
    }

    /**
     * Cree une fausse requete preparee
     */
    public static StubPreparedStatement stubPreparedStatement(int updateCount, List<Map<String, Object>> rows) {
        return new StubPreparedStatement(updateCount, rows);
    }

    /**
     * Cree une connexion JDBC simulee
     */
    public static Connection connectionFor(StubPreparedStatement stub) {
        InvocationHandler handler = (proxy, method, args) -> {
            if (method.getName().equals("prepareStatement") || method.getName().equals("createStatement")) {
                return stub.proxy();
            }
            if (method.getName().equals("close")) {
                return null;
            }
            if (method.getName().equals("isClosed")) {
                return false;
            }
            return defaultValue(method.getReturnType());
        };
        return (Connection) Proxy.newProxyInstance(
                JdbcTestSupport.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                handler);
    }

    /**
     * Cree une connexion BD reutilisable dans les tests
     */
    public static ConnexionMySQL connexionFor(Connection connection) {
        try {
            return new ConnexionMySQL() {
                @Override
                public PreparedStatement prepareStatement(String requete) throws SQLException {
                    return connection.prepareStatement(requete);
                }

                @Override
                public Statement createStatement() throws SQLException {
                    return connection.createStatement();
                }
            };
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0f;
        }
        if (type == double.class) {
            return 0d;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }

    /**
     * Requete preparee simulee
     */
    public static final class StubPreparedStatement {

        private final int updateCount;
        private final List<Map<String, Object>> rows;
        private final Map<Integer, Object> params = new HashMap<>();

        private StubPreparedStatement(int updateCount, List<Map<String, Object>> rows) {
            this.updateCount = updateCount;
            this.rows = rows == null ? List.of() : new ArrayList<>(rows);
        }

        /**
         * Retourne la requete preparee simulee
         */
        public PreparedStatement proxy() {
            InvocationHandler handler = (proxy, method, args) -> {
                if (method.getName().startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer index) {
                    params.put(index, args[1]);
                    return null;
                }
                if (method.getName().equals("executeUpdate")) {
                    return updateCount;
                }
                if (method.getName().equals("executeQuery")) {
                    return resultSetProxy();
                }
                if (method.getName().equals("close") || method.getName().equals("clearParameters")) {
                    if (method.getName().equals("clearParameters")) {
                        params.clear();
                    }
                    return null;
                }
                return defaultValue(method.getReturnType());
            };
            return (PreparedStatement) Proxy.newProxyInstance(
                    JdbcTestSupport.class.getClassLoader(),
                    new Class<?>[]{PreparedStatement.class},
                    handler);
        }

        /**
         * Retourne un parametre enregistre
         */
        public Object param(int index) {
            return params.get(index);
        }

        private ResultSet resultSetProxy() {
            InvocationHandler handler = new InvocationHandler() {
                private int cursor = -1;

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    if (method.getName().equals("next")) {
                        cursor++;
                        return cursor < rows.size();
                    }
                    if (method.getName().equals("getString")) {
                        Object value = value(args[0]);
                        if (value == null) {
                            return null;
                        }
                        if (value instanceof String text) {
                            return text;
                        }
                        return null;
                    }
                    if (method.getName().equals("getInt")) {
                        Object value = value(args[0]);
                        if (value instanceof Number number) {
                            return number.intValue();
                        }
                        if (value == null) {
                            return 0;
                        }
                        return 0;
                    }
                    if (method.getName().equals("getObject")) {
                        return value(args[0]);
                    }
                    if (method.getName().equals("wasNull") || method.getName().equals("close")) {
                        return false;
                    }
                    return defaultValue(method.getReturnType());
                }

                private Object value(Object column) {
                    if (cursor < 0 || cursor >= rows.size()) {
                        return null;
                    }
                    Map<String, Object> row = rows.get(cursor);
                    if (column instanceof String columnName) {
                        return row.get(columnName);
                    }
                    return null;
                }
            };
            return (ResultSet) Proxy.newProxyInstance(
                    JdbcTestSupport.class.getClassLoader(),
                    new Class<?>[]{ResultSet.class},
                    handler);
        }
    }
}
