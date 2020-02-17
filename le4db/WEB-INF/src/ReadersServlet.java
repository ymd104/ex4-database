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
public class ReadersServlet extends HttpServlet {

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

		String readerid = request.getParameter("readerid");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>更新</h3>");
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<form action=\"readerupdate\" method=\"GET\">");
			//out.println("商品ID： " + pid);
			out.println("読者ID： " + readerid);
			//out.println("<input type=\"hidden\" name=\"update_pid\" + value=\"" + pid + "\"/>");
			out.println("<input type=\"hidden\" name=\"update_readerid\" + value=\"" + readerid + "\"/>");
			out.println("<br/>");

			//ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE pid = " + pid);
			ResultSet rs = stmt.executeQuery("SELECT reader_nickname.readerid, readername, password FROM reader_nickname, reader_pass WHERE reader_nickname.readerid = " + readerid + "AND reader_nickname.readerid = reader_pass.readerid");
			while (rs.next()) {
				String nickname = rs.getString("readername");
				String password = rs.getString("password");

				out.println("ユーザ名： ");
				out.println("<input type=\"text\" name=\"update_nickname\" value=\"" + nickname + "\"/>");
				out.println("<br/>");
				out.println("パスワード： ");
				out.println("<input type=\"text\" name=\"update_password\" value=\"" + password + "\"/>");
				out.println("<br/>");

			}
			rs.close();

			out.println("<input type=\"submit\" value=\"更新\"/>");
			out.println("</form>");

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

		out.println("<h3>読書歴</h3>");
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>本ID</th><th>タイトル</th><th>作者</th><th>出版社</th><th>読書歴</th><th>評価</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT books.bookid, title, author, publisher, history, evaluation FROM books, readhistory WHERE readerid = " + readerid + " AND books.bookid = readhistory.bookid");
			while (rs.next()) {
				int bookid = rs.getInt("bookid");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");
				String history = rs.getString("history");
				String evaluation = rs.getString("evaluation");

				out.println("<tr>");
				out.println("<td>" + bookid + "</td>");
				out.println("<td>" + title + "</td>");
				out.println("<td>" + author + "</td>");
				out.println("<td>" + publisher + "</td>");
				out.println("<td>" + history + "</td>");
				out.println("<td>" + evaluation + "</td>");
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

		out.println("<h3>削除</h3>");
		out.println("<form action=\"readerdelete\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"delete_readerid\" value=\"" + readerid + "\">");
		out.println("<input type=\"submit\" value=\"削除\"/>");
		out.println("</form>");

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
