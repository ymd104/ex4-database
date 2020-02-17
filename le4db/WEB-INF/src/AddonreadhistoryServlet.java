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
public class AddonreadhistoryServlet extends HttpServlet {

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

		String deleteReaderID = request.getParameter("delete_readerid");

		out.println("<html>");
		out.println("<body>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);

			//トランザクション処理.
			conn.setAutoCommit(false);

			stmt = conn.createStatement();

			out.println("以下のユーザを削除しました。<br/><br/>");
			out.println("読者ID: " + deleteReaderID + "<br/>");

			ResultSet rs = stmt.executeQuery("SELECT reader_nickname.readerid, readername, password FROM reader_nickname, reader_pass WHERE reader_nickname.readerid = " + deleteReaderID + "AND reader_nickname.readerid = reader_pass.readerid");
			while (rs.next()) {
				String readerid = rs.getString("readerid");
				String nickname = rs.getString("readername");
				String password = rs.getString("password");

				out.println("ユーザ名: " + nickname + "<br/>");
				out.println("パスワード" + password + "<br/>");
			}
			rs.close();

			stmt.executeUpdate("DELETE FROM reader_nickname WHERE readerid=" + deleteReaderID);
			stmt.executeUpdate("DELETE FROM reader_pass WHERE readerid=" + deleteReaderID);
			stmt.executeUpdate("DROP VIEW read" + deleteReaderID);

			//トランザクション処理.
			conn.commit();
			conn.setAutoCommit(true);

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
		out.println("<a href=\"userlist\">ユーザリストに戻る</a>");

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
