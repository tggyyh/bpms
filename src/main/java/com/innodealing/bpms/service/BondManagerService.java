package com.innodealing.bpms.service;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.BondManagerMapper;
import com.innodealing.bpms.model.BondManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Transactional
@Service("bondManagerService")
public class BondManagerService {

    @Autowired
    BondManagerMapper bondManagerMapper;

    //事务
    @Autowired
    DataSourceTransactionManager transactionManager;

    private final static Logger logger = LoggerFactory.getLogger(BondManagerService.class);

    public List<BondManager> findByBondCode(String bondCode){
        List<BondManager> bondManagerList = bondManagerMapper.findByBondCode(bondCode);
        return bondManagerList;
    }

    public List<BondManager> findByBondCodes(ReqData reqData){
        List<BondManager> bondManagerList = bondManagerMapper.findByBondCodes(reqData);
        return bondManagerList;
    }

    public int bondManagerSave(String bondCode, List<BondManager> bondManagerList){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            count = bondManagerMapper.deleteByBondCode(bondCode);
            if(null!=bondManagerList && bondManagerList.size()>0){
                count = bondManagerMapper.insertBondManager(bondManagerList);
            }
            transactionManager.commit(status);
            logger.info("项目负责人保存成功：bondManagerSave");
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("项目负责人保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int insertBondManager(List<BondManager> bondManagerList){
        //事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

        int count = 0;
        try{
            count = bondManagerMapper.insertBondManager(bondManagerList);
            transactionManager.commit(status);
            logger.info("项目负责人保存成功：insertBondManager");
        }catch(Exception ex){
            transactionManager.rollback(status);
            ex.printStackTrace();
            logger.info("项目负责人保存失败:" + ex.getMessage());
        }
        return count;
    }

    public int deleteByBondCode(String bondCode){
        return bondManagerMapper.deleteByBondCode(bondCode);
    }

}
