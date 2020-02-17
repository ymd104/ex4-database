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
public class UserpageServlet extends HttpServlet {

	private String _hostname = null;
	private String _dbname = null;
	private String _username = null;
	private String _password = null;

	public boolean injection(String str) {
		return (str.contains("'") || str.contains("--") || str.contains("/") || str.contains(";") || str.contains(" "));
	}

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

		String readerID = request.getParameter("login_id");
		String readerPassword = request.getParameter("login_password");
		int records = 0;

		out.println("<html>");
		out.println("<body>");

		if (injection(readerID) || injection(readerPassword)){
			out.println("IDもしくはパスワードが違います");
			out.println("<br/>");
			out.println("<a href=\"top\">トップページに戻る</a>");
			out.println("</body>");
			out.println("</html>");
		}
		else {

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT COUNT (*) FROM reader_pass WHERE readerid = " + Integer.parseInt(readerID) + "AND password = '" + readerPassword + "'");
			//SELECT COUNT (*)で条件に合うレコードの数を数えられるらしい. 属性はcountになるっぽい
			while (rs.next()) {
				records = rs.getInt("count");
			}
			rs.close();

			/*
			if (records == 1) {
				String url = "/userpage";
				request.setAttribute("loginID", loginID);
				response.sendRedirect(url);
			}
			else {
				out.println("IDもしくはパスワードが違います");
				out.println("<br/>");
				out.println("<a href=\"top\">トップページに戻る</a>");
			}
			*/

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


		if(records == 1) {
		//request.setAttribute("readerid", "readerID");
		out.println("<h3>プロフィール</h3>");
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>読者ID(ログインID)</th><th>ユーザ名</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT * FROM reader_nickname WHERE readerid = " + readerID );
			while (rs.next()) {
				//int bookid = rs.getInt("bookid");
				String nickname = rs.getString("readername");

				out.println("<tr>");
				out.println("<td>" + readerID + "</td>");
				out.println("<td>" + nickname + "</td>");
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

		//out.println("<br/>");
		//out.println("<a href=\"nicknameedit\">ユーザ名編集</a>");

		out.println("<h3>読書歴</h3>");
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>本ID</th><th>タイトル</th><th>作者</th><th>出版社</th><th>読書歴</th><th>評価</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT books.bookid, title, author, publisher, history, evaluation FROM books, read" + readerID + " WHERE books.bookid = read" + readerID + ".bookid");
			while (rs.next()) {

				int bookid = rs.getInt("bookid");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");
				String history = rs.getString("history");
				int evaluation = rs.getInt("evaluation");

				out.println("<tr>");
				//out.println("<td><a href=\"readbook?bookid=" + bookid + "\">" + bookid
				//		+ "</a></td>");
				out.println("<td>" + bookid + "</td>");
				out.println("</form>");
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

		out.println("<br/>");
		out.println("<h3>更新</h3>");

		out.println("<form action=\"readbook\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"readerid\" value=\" ");
		out.println(readerID);
		out.println("\"/>");
		out.println("<input type=\"hidden\" name=\"password\" value=\" ");
		out.println(readerPassword);
		out.println(""+ "\"/>");
		out.println("<input type=\"submit\" value=\"更新\"/>");
		out.println("</form>");
		out.println("<br/>");
		out.println("<br/>");


		out.println("<h3>ブックサーチ</h3>");
		out.println("<form action=\"searchforreader\" method=\"GET\">");
		out.println("タイトル： ");
		out.println("<input type=\"text\" name=\"search_title\"/>");
		out.println("<br/>");
		out.println("作者： ");
		out.println("<input type=\"text\" name=\"search_author\"/>");
		out.println("<br/>");
		out.println("出版社： ");
		out.println("<input type=\"text\" name=\"search_publisher\"/>");
		out.println("<br/>");

		out.println("<input type=\"hidden\" name=\"readerid\" value=\" ");
		out.println(readerID);
		out.println("\"/>");
		out.println("<br/>");
		out.println("<input type=\"hidden\" name=\"password\" value=\" ");
		out.println(readerPassword);
		out.println(""+ "\"/>");
		out.println("<br/>");

		out.println("<input type=\"submit\" value=\"検索\"/>");
		out.println("</form>");
		out.println("<br/>");



		out.println("<form action=\"userdata\" method=\"GET\">");
		out.println("<input type=\"hidden\" name=\"readerid\" value=\" ");
		out.println(readerID);
		out.println("\"/>");
		out.println("<br/>");
		out.println("<input type=\"hidden\" name=\"password\" value=\" ");
		out.println(readerPassword);
		out.println(""+ "\"/>");
		out.println("<br/>");

		out.println("<input type=\"submit\" value=\"ユーザデータの詳細を閲覧\"/>");
		out.println("</form>");




		out.println("<br/>");
		out.println("<a href=\"top\">ログアウト</a>");

		}else {
			out.println("IDもしくはパスワードが違います");
			out.println("<br/>");
			out.println("<a href=\"top\">トップページに戻る</a>");
			out.println("</body>");
			out.println("</html>");
		}

		out.println("</body>");
		out.println("</html>");
	}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
