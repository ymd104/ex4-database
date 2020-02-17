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
public class UseraddServlet extends HttpServlet {

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

		String addNickname = request.getParameter("add_nickname");
		String addPassword = request.getParameter("add_password");

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

			int max_readerid = 0;
			ResultSet rs = stmt.executeQuery("SELECT MAX(readerid) AS max_readerid FROM reader_nickname");
			while (rs.next()) {
				max_readerid = rs.getInt("max_readerid");

			}
			rs.close();

			int addReaderID = max_readerid + 1;
			stmt.executeUpdate("INSERT INTO reader_nickname VALUES(" + addReaderID + ", '" + addNickname + "')");
			stmt.executeUpdate("INSERT INTO reader_pass VALUES(" + addReaderID + ", '" + addPassword + "')");
			//新規登録の時点で、読者ごとに読んだ本(あるいは積読している本)のビューを作ってしまいます。
			//ビューの名前はread[読者ID]とします。
			stmt.executeUpdate("CREATE VIEW read" + addReaderID + " AS SELECT * FROM readhistory WHERE readerid = " + addReaderID + ";");

			//トランザクション処理.
			conn.commit();
			conn.setAutoCommit(true);

			out.println("以下のユーザ名およびパスワードで、ユーザを新規登録しました<br/><br/>");
			out.println("読者ID: " + addReaderID + "<br/>");
			out.println("ユーザ名: " + addNickname + "<br/>");
			out.println("パスワード: " + addPassword + "<br/>");

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
