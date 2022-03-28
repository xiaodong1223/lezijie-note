package com.leziji.note.dao;

import cn.hutool.core.util.StrUtil;
import com.leziji.note.po.Note;
import com.leziji.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    /**
     * 添加或修改云记，返回受影响的行数
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
        // 定义SQL语句
        String sql = "";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        // 判断noteId是否为空；如果为空，则为添加操作；如果不为空，则为修改操作
        if (note.getNoteId() == null) { // 添加操作
            sql = "insert into tb_note (typeId, title, content, pubTime, lon, lat) values (?,?,?,now(),?,?)";
            //不要写反了
            params.add(note.getLon());
            params.add(note.getLat());
        } else { // 修改操作
            sql = "update tb_note set typeId = ?, title = ? , content = ? where noteId = ?";
            params.add(note.getNoteId());
        }

        // 调用BaseDao的更新方法
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }

    /**
     * 查询当前登录用户的云记数量，返回总记录数
     * @param userId
     * @return
     */
    public long findNoteCount(Integer userId,String title,String date,String typeId) {
        // 定义SQL语句
        String sql = "SELECT count(1) FROM tb_note n INNER JOIN " +
                " tb_note_type t on n.typeId = t.typeId  WHERE userId = ? ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

         //判断条件查询的参数是否为空 （如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) { // 标题查询
            // 拼接条件查询的sql语句
            sql += " and title like concat('%',?,'%') ";
            // 设置sql语句所需要的参数
            params.add(title);

        } else if (!StrUtil.isBlank(date)) { // 日期查询
            // 拼接条件查询的sql语句
            sql += " and date_format(pubTime,'%Y年%m月') = ? ";
            // 设置sql语句所需要的参数
            params.add(date);

        } else if (!StrUtil.isBlank(typeId)) { // 类型查询
            // 拼接条件查询的sql语句
            sql += " and n.typeId = ? ";
            // 设置sql语句所需要的参数
            params.add(typeId);
        }

        // 调用BaseDao的查询方法
        long count = (long) BaseDao.findSingleValue(sql, params);

        return count;
    }


    /**
     * 分页查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId) {
        // 定义SQL语句
        String sql = "SELECT noteId,title,pubTime FROM tb_note n INNER JOIN " +
                "tb_note_type t on n.typeId = t.typeId WHERE userId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);



        //判断条件查询的参数是否为空 （如果查询的参数不为空，则拼接sql语句，并设置所需要的参数）
        if (!StrUtil.isBlank(title)) {
            // 拼接条件查询的sql语句
            sql += " and title like concat('%',?,'%') ";
            // 设置sql语句所需要的参数
            params.add(title);
        } else if (!StrUtil.isBlank(date)) { // 日期查询
            // 拼接条件查询的sql语句
            sql += " and date_format(pubTime,'%Y年%m月') = ? ";
            // 设置sql语句所需要的参数
            params.add(date);
        } else if (!StrUtil.isBlank(typeId)) { // 类型查询
            // 拼接条件查询的sql语句
            sql += " and n.typeId = ? ";
            // 设置sql语句所需要的参数
            params.add(typeId);
        }

        // 拼接分页的sql语句 （limit语句需要写在sql语句最后）
        sql += " order by pubTime desc limit ?,?";
        params.add(index);
        params.add(pageSize);

        // 调用BaseDao的查询方法
        List<Note> noteList = BaseDao.queryRows(sql, params, Note.class);

        return noteList;
    }

    public List<NoteVo> findNoteCountByDate(Integer userId) {
        // 定义SQL语句,注意这个sql语句
        String sql = "SELECT count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n " +
                " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')" +
                " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao的查询方法
        List<NoteVo> list = BaseDao.queryRows(sql, params, NoteVo.class);

        return list;
    }

    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        // 定义SQL语句
        String sql = "SELECT count(noteId) noteCount, t.typeId, typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao的查询方法
        List<NoteVo> list = BaseDao.queryRows(sql, params, NoteVo.class);

        return list;
    }

    /**
     * 通过id查询云记对象
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        // 定义SQL
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n " +
        " inner join tb_note_type t on n.typeId=t.typeId where noteId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);

        // 调用BaseDao的查询方法
        Note note = (Note) BaseDao.queryRow(sql, params, Note.class);

        return note;
    }

    /**
     * 通过noteId删除云记记录，返回受影响的行数
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        String sql = "delete from tb_note where noteId= ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);

        // 调用BaseDao
        int row = BaseDao.executeUpdate(sql, params);

        return row;
    }

    /**
     * 通过用户ID查询云记列表
     * @param userId
     * @return
     */
    public List<Note> queryNoteList(Integer userId) {
        // 定义SQL语句
        String sql = "select lon, lat from  tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao
        List<Note> list = BaseDao.queryRows(sql, params, Note.class);

        return list;
    }
}
