package schizo;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

@SuppressWarnings("serial")
public class JoinGroupServlet extends HttpServlet {
	
	// who knows if you really need this
	static {
		ObjectifyService.register(Schedule.class);
		ObjectifyService.register(GroupSchedule.class);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			String groupUnique = req.getParameter("goupUnique");
			if(groupUnique != null) {
				List<GroupSchedule> groups = ObjectifyService.ofy().load().type(GroupSchedule.class).list();
				for(GroupSchedule gs : groups) {
					if(gs.getUniqueCode().equals(groupUnique)) {
						ofy().delete().entity(gs).now();
						boolean alreadyAMember = gs.addUser(user);
						if(alreadyAMember) {
							// tell user that they are trying to join a schedule they are already a part of
						}
						ofy().save().entity(gs).now();
						break;
					}
				}
			}
		}
		resp.sendRedirect("/display.jsp");
	}
}
