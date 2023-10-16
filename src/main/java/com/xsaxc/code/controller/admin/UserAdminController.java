package com.xsaxc.code.controller.admin;

import com.xsaxc.code.entity.User;
import com.xsaxc.code.service.UserService;
import com.xsaxc.code.util.Const;
import com.xsaxc.code.util.CryptographyUtil;
import com.xsaxc.code.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/10/13 16:11
 * @Description: 管理员用户 控制器
 */
@Controller
@RequestMapping("/admin/user")
public class UserAdminController {

    @Autowired
    private UserService userService;


    @ResponseBody
    @RequestMapping("/list")
    @RequiresPermissions("分页查询用户信息")
    public Map<String, Object> list(User user, @RequestParam(value = "latelyLoginTimes", required = false) String latelyLoginTimes,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> map = new HashMap();
        String s_blatelyLoginTime = null;
        String s_elatelyLoginTime = null;
        if (StringUtil.isNotEmpty(latelyLoginTimes)) {
            String[] loginTimes = latelyLoginTimes.split("-");
            s_blatelyLoginTime = loginTimes[0];
            s_elatelyLoginTime = loginTimes[1];
        }
        map.put("data", userService.list(user, s_blatelyLoginTime, s_blatelyLoginTime, page, pageSize, Sort.Direction.DESC, "registerDate"));
        map.put("total", userService.getCount(user, s_elatelyLoginTime, s_elatelyLoginTime));
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 修改该用户VIP状态
     *
     * @param userId 用户id
     * @param isVip  是否修改
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateVipState")
    @RequiresPermissions("修改用户VIP状态")
    public Map<String, Object> updateVipState(Integer userId, boolean isVip) {
        Map<String, Object> map = new HashMap();
        User oldUser = userService.getById(userId);
        oldUser.setVip(isVip);
        userService.save(oldUser);
        map.put("success", true);
        return map;
    }

    /**
     * 修改用户封禁状态
     *
     * @param userId 用户id
     * @param isOff  是否修改
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateUserState")
    @RequiresPermissions("修改用户封禁状态")
    public Map<String, Object> updateUserState(Integer userId, boolean isOff) {
        Map<String, Object> map = new HashMap();
        User oldUser = userService.getById(userId);
        oldUser.setOff(isOff);
        userService.save(oldUser);
        map.put("success", true);
        return map;
    }

    /**
     * 修改用户封禁状态
     *
     * @param userId 用户id
     * @return
     */
    @ResponseBody
    @RequestMapping("/resetPassword")
    @RequiresPermissions("重置用户密码")
    public Map<String, Object> resetPassword(Integer userId) {
        Map<String, Object> map = new HashMap();
        User oldUser = userService.getById(userId);
        // 重置密码
        oldUser.setPassword(CryptographyUtil.md5("123456", CryptographyUtil.SALT));
        userService.save(oldUser);
        map.put("errorNo", 0);
        return map;
    }


    /**
     * 修改用户VIP等级
     *
     * @param user 用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateVipGrade")
    @RequiresPermissions("修改用户VIP等级")
    public Map<String, Object> updateVipGrade(User user) {
        Map<String, Object> map = new HashMap();
        User oldUser = userService.getById(user.getUserId());
        oldUser.setVipGrade(user.getVipGrade());
        userService.save(oldUser);
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 用户充值积分
     *
     * @param user 用户
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatePoints")
    @RequiresPermissions("用户充值积分")
    public Map<String, Object> updatePoints(User user) {
        Map<String, Object> map = new HashMap();
        User oldUser = userService.getById(user.getUserId());
        oldUser.setPoints(user.getPoints() + oldUser.getPoints());
        userService.save(oldUser);
        map.put("errorNo", 0);
        return map;
    }

    /**
     * 管理员修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    @ResponseBody
    @RequestMapping("/modifyPassword")
    @RequiresPermissions("管理员修改密码")
    public Map<String, Object> modifyPassword(String oldPassword, String newPassword, HttpSession session) {
        Map<String, Object> map = new HashMap();


        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (!user.getPassword().equals(CryptographyUtil.md5(oldPassword,CryptographyUtil.SALT))){
            map.put("success",false);
            map.put("errorInfo","原密码错误！！");
            return map;
        }
        User oldUser = userService.getById(user.getUserId());
        oldUser.setPassword(CryptographyUtil.md5(newPassword,CryptographyUtil.SALT));
        userService.save(oldUser);
        map.put("success",true);
        return map;
    }

    /**
     * 管理员安全退出
     */
    @RequestMapping("/logout")
    @RequiresPermissions("管理员安全退出")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "redirect:/admin/login.html";
    }

}
