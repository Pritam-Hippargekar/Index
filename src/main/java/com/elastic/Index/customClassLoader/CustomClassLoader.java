//package com.elastic.Index.customClassLoader;
//
//import java.sql.SQLException;
//
//public class CustomClassLoader extends ClassLoader{
//
//    ClassLoader parent;
//    private String connectionString;
//
//    public CustomClassLoader(String connectionString) {
//        this(ClassLoader.getSystemClassLoader(), connectionString);
//    }
//
//    public CustomClassLoader(ClassLoader parent, String connectionString) {
//        super(parent);
//        this.parent = parent;
//        this.connectionString = connectionString;
//    }
//
////    @Override
////    protected Class<?> findClass(String name) throws ClassNotFoundException {
//
//    @Override
//    public Class<?> loadClass(String name) throws ClassNotFoundException {
//        Class cls = null;
//        try {
//            cls = parent.loadClass(name);
//        } catch (ClassNotFoundException cnfe) {
//            byte[] bytes = new byte[0];
//            try {
//                bytes = loadClassFromDatabase(name);
//            } catch (SQLException sqle) {
//                throw new ClassNotFoundException("Unable to load class", sqle);
//            }
//            return defineClass(name, bytes, 0, bytes.length);
//        }
//        return cls;
//    }
//
//    private byte[] loadClassFromDatabase(String name) throws SQLException, ClassNotFoundException {
//        PreparedStatement pstmt = null;
//        Connection connection = null;
//        byte[] data = null;
//        try {
//            connection = DriverManager.getConnection(connectionString);
//
//            String sql = "select class from CLASSES where ClassName= ?";
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setString(1, name);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                Blob blob = rs.getBlob(1);
//                data = blob.getBytes(1, (int) blob.length());
//                return data;
//            }
//        } catch (SQLException sqlex) {
//            System.out.println("Unexpected exception: " + sqlex.toString());
//        } catch (Exception ex) {
//            System.out.println("Unexpected exception: " + ex.toString());
//        } finally {
//            if (pstmt != null) pstmt.close();
//            if (connection != null) connection.close();
//        }
//
//        if(data == null){
//            throw new ClassNotFoundException();
//        }
//        return data;
//    }
//}




//        Main Class:
//        import edu.nraj.IQuote;
//// run with -Djava.library.path=path-to-quth-DLL (e,g, C:\demos\LoadingFromDb\Client\lib\x64
//public class Main {
//    public static void main(String[] args) {
//        try {
//            SqlServerClassLoader cl = new SqlServerClassLoader("jdbc:sqlserver://localhost\\SQLExpress;databaseName=classloading;integratedSecurity=true");
//            Class clazz = cl.findClass("edu.nraj.Quote");
//            IQuote quote = (IQuote) clazz.newInstance();
//            System.out.println(quote.getQuote());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//}
