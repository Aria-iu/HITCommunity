package org.hit.hitcommunitybackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hit.hitcommunitybackend.domain.User;
import org.hit.hitcommunitybackend.model.Result;
import org.hit.hitcommunitybackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:5599")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    public static final String SESSION_NAME = "user";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User u = userService.userRegisterService(user.getUname(), user.getUpassword());
        Result<User> result = new Result<>();
        if(u == null) {
            result.setResultFailed("创建用户失败");
        } else {
            u.setUpassword("");
            result.setResultSuccess("创建用户成功", u);
        }
        return result;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user, HttpServletRequest request) {
        Result<User> result = new Result<>();
        User u = userService.userLoginService(user.getUname(), user.getUpassword());
        if(u == null) {
            result.setResultFailed("登录失败");
        } else {
            User x = new User();
            x.setUname(user.getUname());
            x.setUpassword(user.getUpassword());
            x.setUid(u.getUid());
            request.getSession().setAttribute(SESSION_NAME, x);
            u.setUpassword("");
            result.setResultSuccess("登录成功", u);
        }
        return result;
    }

    @GetMapping("/is-login")
    public Result<User> isLogin(HttpServletRequest request) {
        Result<User> result = new Result<>();
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        // 若session中没有用户信息这说明用户未登录
        if (u == null) {
            result.setResultFailed("未登录");
        } else {
            User x = new User();
            x.setUname(u.getUname());
            x.setUid(u.getUid());
            x.setUpassword("");
            result.setResultSuccess("已登录", x);
        }
        return result;
    }

    @PutMapping("/update/username")
    public Result<Void> updateUsername(@RequestParam String uname, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        User u1 = userService.userUpdateUnameService(u.getUid(), uname);
        Result<Void> result = new Result<>();
        if(u1 == null) {
            result.setResultFailed("更新用户名失败");
        } else {
            request.getSession().setAttribute(SESSION_NAME, u1);
            result.setResultSuccess("更新用户名成功");
        }
        return result;
    }

    @PutMapping("/update/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(Objects.equals(u.getUpassword(), oldPassword)) {
            User u1 = userService.userUpdateUpasswordService(u.getUid(), newPassword);
            if(u1 == null) {
                result.setResultFailed("更新用户密码失败");
            } else {
                result.setResultSuccess("更新用户密码成功");
                request.getSession().setAttribute(SESSION_NAME, u1);
            }
        } else {
            result.setResultFailed("用户密码错误");
        }
        return result;
    }

    @DeleteMapping("/delete/user")
    public Result<Void> deleteUser(@PathVariable Integer uid, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(userService.userDeleteService(u.getUid())) {
            result.setResultSuccess("删除用户成功");
        } else {
            result.setResultFailed("删除用户失败");
        }
        return result;
    }

    @DeleteMapping("/friends/{uid}")
    public Result<Void> deleteFriend(@PathVariable Integer uid, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(userService.userDeleteFriendService(u.getUid(), uid)) {
            result.setResultSuccess("删除好友成功");
        } else {
            result.setResultFailed("删除好友失败");
        }
        return result;
    }

    @PostMapping("/friend-requests/send/{ruid}")
    public Result<Void> sendFriendRequest(@PathVariable Integer ruid, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(userService.friendRequestSentService(u.getUid(), ruid)) {
            result.setResultSuccess("发送申请成功");
        } else {
            result.setResultFailed("发送申请失败");
        }
        return result;
    }

    @PostMapping("/friend-requests/accept/{suid}")
    public Result<Void> acceptFriendRequest(@PathVariable Integer suid, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(userService.friendRequestAccepteService(suid, u.getUid())) {
            result.setResultSuccess("接受申请成功");
        } else {
            result.setResultFailed("接受申请失败");
        }
        return result;
    }

    @PostMapping("/friend-requests/reject/{suid}")
    public Result<Void> rejectFriendRequest(@PathVariable Integer suid, HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<Void> result = new Result<>();
        if(userService.friendRequestRejectService(suid, u.getUid())) {
            result.setResultSuccess("接受申请成功");
        } else {
            result.setResultFailed("接受申请失败");
        }
        return result;
    }

    @GetMapping("/friends")
    public Result<List<User>> getAllFriends(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<List<User>> result = new Result<>();
        List<User> friends = userService.getAllFriendService(u.getUid());
        if(friends != null) {
            for(User user : friends) {
                user.setUpassword("");
            }
            result.setResultSuccess("获取好友成功", friends);
        } else {
            result.setResultFailed("获取好友失败");
        }
        return result;
    }

    @GetMapping("/friend-requests")
    public Result<List<User>> getAllFriendRequests(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute(SESSION_NAME);
        Result<List<User>> result = new Result<>();
        List<User> users = userService.getAllFriendRequestService(u.getUid());
        if(users != null) {
            result.setResultSuccess("获取好友申请成功", users);
        } else {
            result.setResultFailed("获取好友申请失败");
        }
        return result;
    }

    @GetMapping("/find/uname/{uname}")
    public Result<User> findUserByUname(@PathVariable String uname, HttpServletRequest request) {
        Result<User> result = new Result<>();
        User user = userService.userFindByUnameService(uname);
        if(user != null) {
            user.setUpassword("");
            result.setResultSuccess("查询朋友成功", user);
        } else {
            result.setResultFailed("用户不存在");
        }
        return result;
    }

    @GetMapping("/find/uid/{uid}")
    public Result<User> findUserByUid(@PathVariable Integer uid, HttpServletRequest request) {
        Result<User> result = new Result<>();
        User user = userService.userFindByUidService(uid);
        if(user != null) {
            user.setUpassword("");
            result.setResultSuccess("查询朋友成功", user);
        } else {
            result.setResultFailed("用户不存在");
        }
        return result;
    }
}