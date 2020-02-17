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
public class SearchServlet extends HttpServlet {

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

		String searchTitle = request.getParameter("search_title");
		String searchAuthor = request.getParameter("search_author");
		String searchPublisher = request.getParameter("search_publisher");

		out.println("<html>");
		out.println("<body>");

		out.println("<h3>検索結果</h3>");
		out.println("<br/>");
		out.println("タイトル：" + searchTitle);
		out.println("<br/>");
		out.println("作者：" + searchAuthor);
		out.println("<br/>");
		out.println("出版社：" + searchPublisher);

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			//out.println("<tr><th>商品ID</th><th>商品名</th><th>価格</th></tr>");
			out.println("<tr><th>本ID</th><th>タイトル</th><th>作者</th><th>出版社</th></tr>");

			ResultSet rs = stmt
/*					.executeQuery("SELECT * FROM products WHERE name LIKE '%"
							+ searchName + "%'");
							*/
					.executeQuery("SELECT * FROM books WHERE title LIKE '%" + searchTitle + "%'"
							+ "AND author LIKE '%" + searchAuthor + "%'"
							+ "AND publisher LIKE '%" + searchPublisher + "%'");
			while (rs.next()) {
				int bookid = rs.getInt("bookid");
				String title = rs.getString("title");
				String author = rs.getString("author");
				String publisher = rs.getString("publisher");
				//int price = rs.getInt("price");

				out.println("<tr>");
				out.println("<td>" + bookid + "</td>");
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
