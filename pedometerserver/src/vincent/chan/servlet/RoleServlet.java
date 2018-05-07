package vincent.chan.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import vincent.chan.pojo.Role;
import vincent.chan.pojo.User;
import vincent.chan.services.RoleService;
import vincent.chan.services.UserService;
import vincent.chan.utils.Tool;
import cn.itcast.servlet.BaseServlet;

import com.google.gson.Gson;

public class RoleServlet extends BaseServlet {

	public String myRole(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		RoleService roleService = new RoleService();
		User user = (User) request.getSession().getAttribute("user");
		List<Role> role = null;
		if (user != null) {
			role = roleService.getMyRole(user.getUid());
		}
		JSONArray array = JSONArray.fromObject(role);
		response.getOutputStream().write(array.toString().getBytes());
		return null;

	}

	public String getRoles(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		UserService userService = new UserService();
		RoleService roleService = new RoleService();
		String id = request.getSession().getId();
		System.out.println("getRoles:" + id);
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(false);
		if (session == null) {
			if (Tool.isEmpty(username) || Tool.isEmpty(password)) {
				response.getOutputStream().write("faild".getBytes());
			} else {
				searchRole(response, userService, roleService, username,
						password);
			}
			return null;
		} else {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				searchRole(response, userService, roleService, username,
						password);
				return null;
			}
			List<User> userList = roleService.getRole(user.getUid());
			Gson gson = new Gson();
			String json = gson.toJson(userList);
			response.getOutputStream().write(json.getBytes());
		}

		return null;
	}

	private void searchRole(HttpServletResponse response,
			UserService userService, RoleService roleService, String username,
			String password) throws IOException {
		User user = userService.findUserByNameAndPassword(username, password);
		if (user == null) {
			response.getOutputStream().write("faild".getBytes());
		} else {
			List<User> userList = roleService.getRole(user.getUid());
			Gson gson = new Gson();
			String json = gson.toJson(userList);
			System.out.println(json);
			response.getOutputStream().write(json.getBytes());
		}
	}

	public String levelRole(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		UserService userService = new UserService();
		RoleService roleService = new RoleService();
		String id = request.getSession().getId();
		System.out.println("levelRole:" + id);
		HttpSession session = request.getSession(false);
		String steps = request.getParameter("steps");
		String username = request.getParameter("name");
		String password = request.getParameter("password");
		if (session == null) {
			if (Tool.isEmpty(username) || Tool.isEmpty(password)) {
				response.getOutputStream().write("faild".getBytes());
			} else {
				User user = userService.findUserByNameAndPassword(username,
						password);
				if (user == null) {
					response.getOutputStream().write("faild".getBytes());
				} else {

				}
			}
			return null;
		} else {
			User user = (User) session.getAttribute("user");
			if (user == null) {
				User user1 = userService.findUserByNameAndPassword(username,
						password);
				session.setAttribute("user", user1);
				if (user1 == null) {
					response.getOutputStream().write("faild".getBytes());
					return null;
				} else {
					String level = roleService
							.updateRole(steps, user1.getUid());
					userService.updateTotalstepsByUid(user1.getUid(), steps);
					response.getOutputStream().write(level.getBytes());
				}
			} else {
				String level = roleService.updateRole(steps, user.getUid());
				userService.updateTotalstepsByUid(user.getUid(), steps);
				response.getOutputStream().write(level.getBytes());
			}
		}

		return null;
	}

	public String getMyRole(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		RoleService roleService = new RoleService();
		String username = request.getParameter("name");
		String password = request.getParameter("password");
		User user = new User();
		user.setName(username);
		user.setPassword(password);
		User myRole = roleService.getMyRole(user);
		Gson gson = new Gson();
		String json = gson.toJson(myRole);
		response.getOutputStream().write(json.getBytes());
		return null;
	}
}
