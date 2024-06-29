package org.hit.hitcommunitybackend.service.serviceimpl;

import jakarta.annotation.Resource;
import org.hit.hitcommunitybackend.domain.*;
import org.hit.hitcommunitybackend.repository.*;
import org.hit.hitcommunitybackend.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    @Resource
    private PostDao postDao;
    @Resource
    private UserDao userDao;
    @Resource
    private LikeDao likeDao;
    @Resource
    private FriendDao friendDao;

    @Resource
    private CommentDao commentDao;
    @Autowired
    private RepostDao repostDao;
    @Autowired
    private ImageDao imageDao;

    @Override
    public Post postPublishService(Integer uid, Post post) {
        Optional<User> u = userDao.findById(uid);
        post.setPowner(u.orElse(null));
        return postDao.save(post);
    }

    @Override
    public Integer postLikeService(Integer uid, Integer pid) {
        Like like = new Like();
        Optional<User> ret = userDao.findById(uid);
        Optional<Post> post = postDao.findById(pid);
        if (ret.isPresent()) {
            User user = ret.get();
            like.setUser(user);
        }else{
            log.error("User not found in like service");
            return null;
        }
        if (post.isPresent()) {
            Post post1 = post.get();
            like.setPost(post1);
        }else{
            log.error("Post not found in like service");
            return null;
        }
        likeDao.save(like);

        return like.getLid();
    }

    @Override
    public Integer postCommentService(Integer uid, Integer pid, Comment comment) {
        Optional<User> ret = userDao.findById(uid);
        Optional<Post> post = postDao.findById(pid);
        if (ret.isPresent()) {
            User user = ret.get();
            comment.setUser(user);
        }else{
            log.error("User not found in post_comment service");
            return null;
        }
        if (post.isPresent()) {
            Post post1 = post.get();
            comment.setPost(post1);
        }else{
            log.error("Post not found in post_comment service");
            return null;
        }
        commentDao.save(comment);
        return comment.getCid();
    }

    @Override
    public Repost repostService(Integer uid, Integer pid) {
        Optional<Post> post = postDao.findById(pid);
        Optional<User> user = userDao.findById(uid);
        Repost repost = new Repost();
        if (post.isPresent()) {
            Post post1 = post.get();
            repost.setOriginalPost(post1);
        }else{
            log.error("Post not found in post_repost");
            return null;
        }
        if (user.isPresent()) {
            User user1 = user.get();
            repost.setRowner(user1);
        }else{
            log.error("User not found in post_repost");
            return null;
        }
        Repost rp =  repostDao.save(repost);
        return rp;
    }

    @Override
    public List<Post> getAllPost(Integer uid) {
        List<Post> posts =  postDao.findAll();
        List<Post> needs = new ArrayList<>();
        for (Post post : posts) {
            post.getPowner().setUpassword("");
            if(post.getPowner().getUid()==uid){
                needs.add(post);
            }
        }
        return needs;
    }

    @Override
    public Optional<Post> getPostById(Integer pid) {
        Optional<Post> res = postDao.findById(pid);
        if (res.isPresent()) {
            res.get().getPowner().setUpassword("");
            return res;
        }else{
            log.error("Post not found in  getPostById()");
            return Optional.empty();
        }
    }
    
    @Override
    public List<Post> getAllofPost(){
        List<Post> posts =  postDao.findAll();
        for (Post post : posts) {
            post.getPowner().setUpassword("");
        }
        return posts;
    }

    @Override
    public boolean uploadImageService(Integer pid, String url) {
        try {
            // 创建新的 Image 实体
            Image image = new Image(pid, url);
            // 保存 Image 实体到数据库
            imageDao.save(image);
            return true; // 成功保存，返回 true
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 保存失败，返回 false
        }
    }

    @Override
    public List<String> getImagesService(Integer pid) {
        // 使用 ImageDao 查找所有关联的 Image 实体
        List<Image> images = imageDao.findByPid(pid);
        // 提取每个 Image 实体的 iurl 字段并返回
        return images.stream()
                .map(Image::getIurl)
                .collect(Collectors.toList());
    }

    @Override
    public List<Repost> getRepostByUId(Integer uid) {
      return repostDao.findAllByRowner_Uid(uid);
    }


    @Override
    public void adminDeletePostService(Integer pid) {
        commentDao.deleteCommentsByPostId(pid);
        likeDao.deleteByPostId(pid);
        repostDao.deleteByPostId(pid);
        imageDao.deleteByPostId(pid);
        postDao.deleteById(pid);
    }

    @Override
    public void userDeletePostService(Integer pid) {
        commentDao.deleteCommentsByPostId(pid);
        likeDao.deleteByPostId(pid);
        repostDao.deleteByPostId(pid);
        imageDao.deleteByPostId(pid);
        postDao.deleteById(pid);
    }

    @Override
    public List<Post> adminGetAllPost() {
        return postDao.findAll();
    }

    @Override
    public void userDeleteRepostService(Integer rid) {
        repostDao.deleteById(rid);
    }

    @Override
    public List<Post> userGetCircleService(Integer uid) {
        Set<Post> posts = new HashSet<>();
        List<User> friends = friendDao.findAllFriend(uid);
        for (User f : friends) {
            List<Post> fps = postDao.findByPid(f.getUid());
            List<Repost> frps = repostDao.findAllByRowner_Uid(f.getUid());
            posts.addAll(fps);
            for(Repost r : frps) {
                posts.add(r.getOriginalPost());
            }
        }
        List<Post> mps = postDao.findByPowner_Uid(uid);
        posts.addAll(mps);
        List<Post> l = new ArrayList<>(posts);
        for(Post p : l) {
            p.getPowner().setUpassword("");
        }
        return l;
    }
}