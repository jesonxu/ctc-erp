var BILL_OPERATIONS = {
    100: {
        title: '删除账单',
        desc: '删除账单记录。如果有的话，同时删除运营成本、账单罚息、实际提成，还原到款',
        operation: '/bill/operateDeleteBill'
    },
    101: {
        title: '修改账单数据',
        desc: '修改账单数据，比如条数、金额，以满足销账',
        operation: '/bill/operateModifyBill'
    },
    200: {
        title: '修改为未对账',
        desc: '账单状态改为未对账，如果有的话，同时删除运营成本、账单罚息、实际提成，还原到款',
        operation: '/bill/operateBillUnChecked'
    },
    201: {
        title: '删除对账流程',
        desc: '删除仅此账单对账的流程，如果是多个账单一起对账，将不会删除流程',
        operation: '/bill/operateDeleteCheckFlow'
    },
    300: {
        title: '修改为未销账(已对账)',
        desc: '账单状态改为未销账(即已对账)，如果有的话，同时删除运营成本、实际提成，还原到款',
        operation: '/bill/operateBillUnWriteOff'
    },
    301: {
        title: '删除销账流程',
        desc: '删除仅此账单销账的流程，如果是多个账单一起销账，将不会删除流程',
        operation: '/bill/operateDeleteWriteOffFlow'
    },
    302: {
        title: '还原关联的到款',
        desc: '还原到款被销账账单减去的金额，清空账单的关联信息',
        operation: '/bill/operateReleaseIncome'
    },
    303: {
        title: '删除实际提成',
        desc: '删除实际提成。销账流程归档后，第二天会计算实际提成',
        operation: '/bill/operateDeleteRealRoyalty'
    },
    304: {
        title: '删除运营成本',
        desc: '删除运营成本。销账流程归档时，计算账单的运营成本',
        operation: '/bill/operateDeleteOperateCost'
    },
    305: {
        title: '删除账单当月罚息',
        desc: '删除账单在当前月的罚息。定时任务计算当月罚息，销账流程归档时，确定账单的当月罚息',
        operation: '/bill/operateDeletePenaltyInterest'
    },
    306: {
        title: '删除账单所有罚息',
        desc: '删除账单每月的罚息。',
        operation: '/bill/operateDeleteAllPenaltyInterest'
    }
}