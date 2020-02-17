import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UserdataServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public void init() throws ServletException {
		// iniファイルから自分のデータベース情報を読み込む
		String iniFilePath = getServletConfig().getServletContext()
				.getRealPath("WEB-INF/le4db.ini");
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			Properties prop = new Properties();
			prop.load(fis);
			_hostname = prop.getProperty("hostname");
			_dbname = prop.getProperty("dbname");
			_username = prop.getProperty("username");
			_password = prop.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		String readerID = request.getParameter("readerid");
		String password = request.getParameter("password");
		int records = 0;

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();


		out.println("読了した本の総数：");
		ResultSet rs = stmt.executeQuery("SELECT COUNT (*) FROM readhistory WHERE history = '読了' AND readerid = " + readerID);
		while (rs.next()) {
			records = rs.getInt("count");
		}
		rs.close();
		out.println(records);
		out.println("冊");
		out.println("<br/>");



		out.println("積読中の本の総数：");
		rs = stmt.executeQuery("SELECT COUNT (*) FROM readhistory WHERE history = '積読' AND readerid = " + readerID);
		while (rs.next()) {
			records = rs.getInt("count");
		}
		rs.close();

		out.println(records);
		out.println("冊");
		out.println("<br/>");





		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		out.println("<br/>");
		out.println("<a href=\"userpage?login_id=" + Integer.parseInt(readerID.substring(3, readerID.length()-2)) + "&login_password=" + password.substring(3, password.length()) + "\">ユーザページに戻る</a>");

		out.println("</body>");
		out.println("</html>");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
