package com.example.service;

import com.example.entity.Certification;
import com.example.mapper.CertificationMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务处理
 **/
@Service
public class CertificationService {

    @Resource
    private CertificationMapper certificationMapper;

    /**
     * 新增
     */
    public void add(Certification certification) {
        certificationMapper.insert(certification);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        certificationMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            certificationMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Certification certification) {
        certificationMapper.updateById(certification);
    }

    /**
     * 根据ID查询
     */
    public Certification selectById(Integer id) {
        return certificationMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Certification> selectAll(Certification certification) {
        return certificationMapper.selectAll(certification);
    }

    /**
     * 分页查询
     */
    public PageInfo<Certification> selectPage(Certification certification, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Certification> list = certificationMapper.selectAll(certification);
        return PageInfo.of(list);
    }

}
