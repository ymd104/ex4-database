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
public class UserlistServlet extends HttpServlet {

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

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>ユーザリスト</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>読者ID</th><th>ユーザ名</th><th>パスワード</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT reader_nickname.readerid, readername, password FROM reader_nickname, reader_pass WHERE reader_nickname.readerid = reader_pass.readerid");
			while (rs.next()) {
				int readerID = rs.getInt("readerid");
				String readerName = rs.getString("readername");
				String readerPassword = rs.getString("password");

				out.println("<tr>");
				/*out.println("<td><a href=\"item?readerid=" + readerID + "\">" + readerID
						+ "</a></td>");
				今後拡張する際に, 管理者からユーザごとの読書歴や評価を見られるようにするためには,
				ここの"item"を別のページにする必要がある
						*/
				//out.println("<td>" + readerID + "</td>");
				out.println("<td><a href=\"readers?readerid=" + readerID + "\">" + readerID
						+ "</a></td>");
				out.println("<td>" + readerName + "</td>");
				out.println("<td>" + readerPassword + "</td>");
				out.println("</tr>");
			}
			rs.close();

			out.println("</table>");

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

		out.println("<h3>新規登録</h3>");
		out.println("<form action=\"useradd\" method=\"GET\">");
		//useraddページに飛ばす
		out.println("ユーザ名： ");
		out.println("<input type=\"text\" name=\"add_nickname\"/>");
		out.println("<br/>");
		out.println("パスワード： ");
		out.println("<input type=\"text\" name=\"add_password\"/>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"登録\"/>");
		out.println("</form>");


		out.println("<br/>");
		out.println("<a href=\"list\">管理者用ページに戻る</a>");

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
