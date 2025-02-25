import request from '../utils/request'

export default {
  // 创建请假申请
  createLeave: (data) => request.post('/leaves', data),
  
  // 审批请假
  approveLeave: (leaveId, data) => request.post(`/leaves/${leaveId}/approve`, data),
  
  // 获取我的请假记录
  getMyLeaves: () => request.get('/leaves/my'),
  
  // 获取待我审批的请假
  getPendingApprovals: () => request.get('/leaves/pending'),
  
  // 获取部门请假记录
  getDepartmentLeaves: (deptId) => request.get(`/leaves/department/${deptId}`),
  
  // 获取请假详情
  getLeaveById: (leaveId) => request.get(`/leaves/${leaveId}`)
} 