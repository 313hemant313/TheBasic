package ya.boilerplate.thebasic.helper;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.model.UserDetailView;
import ya.boilerplate.thebasic.security.OurUserDetails;

@Service
public class BasicUtility {

	public Date convertStringToDate(String strDate) throws Exception {
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		return date1;
	}
	
	public Integer getUserIdFromToken() {

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			Integer id = ((OurUserDetails) principal).getId();
			return id;
		}

		return null;
	}

	public UserDetailView convertUserEntityToView(User userEntity)
			throws IllegalArgumentException, IllegalAccessException, NullPointerException {

		UserDetailView userDetailView = new UserDetailView();
		List<Field> fields = new ArrayList<>();
		fields = Arrays.asList(UserDetailView.class.getDeclaredFields());

		for (Field field : fields) {
			Field fieldToUpdate = ReflectionUtils.findField(User.class, field.getName());
			if (null != fieldToUpdate) {
				field.setAccessible(true);
				fieldToUpdate.setAccessible(true);
				if (null == fieldToUpdate.get(userEntity))
					continue;

				ReflectionUtils.setField(field, userDetailView, fieldToUpdate.get(userEntity));
			}
		}

		return userDetailView;
	}

}
