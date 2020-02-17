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
public class ListServlet extends HttpServlet {

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

		out.println("<h3>タイトル検索</h3>");
		out.println("<form action=\"search\" method=\"GET\">");
		out.println("タイトル： ");
		out.println("<input type=\"text\" name=\"search_title\"/>");
		out.println("<br/>");
		out.println("作者： ");
		out.println("<input type=\"text\" name=\"search_author\"/>");
		out.println("<br/>");
		out.println("出版社： ");
		out.println("<input type=\"text\" name=\"search_publisher\"/>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"検索\"/>");
		out.println("</form>");

		out.println("<h3>ブックリスト</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>本ID</th><th>タイトル</th><th>作者</th><th>出版社</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM books");
			while (rs.next()) {
				int bookid = rs.getInt("bookid");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");

				out.println("<tr>");
				out.println("<td><a href=\"item?bookid=" + bookid + "\">" + bookid
						+ "</a></td>");
				out.println("<td>" + title + "</td>");
				out.println("<td>" + author + "</td>");
				out.println("<td>" + publisher + "</td>");
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

		out.println("<h3>追加</h3>");
		out.println("<form action=\"add\" method=\"GET\">");
		out.println("タイトル： ");
		out.println("<input type=\"text\" name=\"add_title\"/>");
		out.println("<br/>");
		out.println("作者： ");
		out.println("<input type=\"text\" name=\"add_author\"/>");
		out.println("<br/>");
		out.println("出版社： ");
		out.println("<input type=\"text\" name=\"add_publisher\"/>");
		out.println("<br/>");
		out.println("<input type=\"submit\" value=\"追加\"/>");
		out.println("</form>");

		out.println("<br/>");
		out.println("<a href=\"userlist\">ユーザリスト</a>");

		out.println("<br/>");
		out.println("<a href=\"top\">トップページへ</a>");

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
