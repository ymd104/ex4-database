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
public class ReadbookServlet extends HttpServlet {

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

		String readerID = request.getParameter("readerid");
		String password = request.getParameter("password");

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<body>");

		out.println("このページでは、読書歴・評価の更新、リストへの本の追加、リストからの本の削除が行えます。");
		out.println("<br/>");
		out.println("更新の際には、全てのテキストボックスに情報を入力してください。");
		out.println("<br/>");
		out.println("本の削除の際には、読書歴を「未読」にすることでリストから削除できます。");
		out.println("<br/>");
		out.println("評価として反映されるのは1から5までの値のみです。0は未評価という扱いになります。");
		out.println("<br/>");

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
					+ ":5432/" + _dbname, _username, _password);
			stmt = conn.createStatement();

			out.println("<table border=\"1\">");
			out.println("<tr><th>本ID</th><th>タイトル</th><th>作者</th><th>出版社</th><th>読書歴</th><th>評価</th></tr>");

			ResultSet rs = stmt.executeQuery("SELECT books.bookid, title, author, publisher, history, evaluation FROM books, read" + readerID.substring(3, readerID.length()-2) + " WHERE books.bookid = read" + readerID.substring(3, readerID.length()-2) + ".bookid");
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


		out.println("<h3>更新</h3>");
		out.println("<form action=\"readupdate\" method=\"GET\">");
		out.println("本ID： ");
		out.println("<input type=\"text\" name=\"update_bookid\"/>");
		out.println("<br/>");
		out.println("読書歴： ");
		out.println("<input type=\"radio\" name=\"update_history\" value='未読'/>");
		out.println("未読");
		out.println("<input type=\"radio\" name=\"update_history\" value='積読'/>");
		out.println("積読");
		out.println("<input type=\"radio\" name=\"update_history\" value='読了'/>");
		out.println("読了");
		out.println("<br/>");
		out.println("評価： ");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=0 />");
		out.println("0");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=1 />");
		out.println("1");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=2 />");
		out.println("2");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=3 />");
		out.println("3");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=4 />");
		out.println("4");
		out.println("<input type=\"radio\" name=\"update_evaluation\" value=5 />");
		out.println("5");
		out.println("<br/>");

		out.println("<input type=\"hidden\" name=\"readerid\" value=\" ");
		out.println(readerID.substring(3, readerID.length()-2));
		out.println("\"/>");
		out.println("<br/>");
		out.println("<input type=\"hidden\" name=\"password\" value=\" ");
		out.println(password.substring(3, password.length()-2));
		out.println(""+ "\"/>");
		out.println("<br/>");

		out.println("<input type=\"submit\" value=\"更新\"/>");
		out.println("</form>");

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
