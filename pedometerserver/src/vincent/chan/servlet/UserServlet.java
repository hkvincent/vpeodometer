package vincent.chan.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import vincent.chan.pojo.User;
import vincent.chan.services.UserService;
import vincent.chan.utils.Tool;

import cn.itcast.servlet.BaseServlet;

public class UserServlet extends BaseServlet {

	public String login(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		UserService userService = new UserService();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		System.out.println(name + password);
		User gotUser = userService.findUserByNameAndPassword(name, password);
		if (gotUser == null) {
			response.getOutputStream().write(
					"login failed:check your username and password".getBytes());
			return null;
		}
		request.getSession().setAttribute("user", gotUser);
		System.out.println("login:" + request.getSession(false).getId());
		response.getOutputStream().write("login success".getBytes());
		return null;

	}

	public String register(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		UserService userService = new UserService();
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String gender = request.getParameter("gender");
		long registerTime = System.currentTimeMillis() / 1000;

		User user = new User();
		user.setUid(Tool.randomId(6));
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);
		user.setRegisterTime(registerTime);
		user.setGender(gender.equals("male") ? 1 : 0);
		System.out.println(name + password);
		boolean createUser = userService.createUser(user);
		if (createUser) {
			response.getOutputStream().write("register success".getBytes());
		} else {
			response.getOutputStream().write("register faild".getBytes());
		}

		return null;
	}

	public String updataPassword(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		UserService userService = new UserService();
		String id = request.getSession().getId();
		System.out.println("updataPassword:" + id);
		HttpSession session = request.getSession(false);
		String username = request.getParameter("name");
		String password = request.getParameter("password");
		String newPassword = request.getParameter("newPassword");
		String updatePassword = "falid";
		User user = new User();
		user.setName(username);
		user.setPassword(password);
		updatePassword = userService.updatePassword(user, newPassword);
		response.getOutputStream().write(updatePassword.getBytes());
		return null;
	}
}
