package com.backend.ems.Aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.backend.ems.Entity.Leave;
import com.backend.ems.Repository.LeaveRequestRepository;
import com.backend.ems.Service.Service_implementation.EmailServiceImpl;
import com.backend.ems.Service.Service_implementation.LeaveServiceImpl;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LeaveApprovalAspect {
    // private final LeaveRequestRepository leaveRequestRepository;
    // private final LeaveServiceImpl leaveServiceImpl;
    // private final EmailServiceImpl emailServiceImpl;

    // @Pointcut("execution(*
    // com.backend.ems.Service.Service_implementation.LeaveServiceImpl.approveLeave(..))
    // && args(id)")
    // public void approveLeavePointcut(int id) {
    // }

    // @Pointcut("execution(*
    // com.backend.ems.Service.Service_implementation.LeaveServiceImpl.rejectLeave(..))
    // && args(id)")
    // public void rejectLeavePointcut(int id) {
    // }

    // @After("approveLeavePointcut(id)")
    // public void afterLeaveApproval(int id) {
    // Leave al = leaveRequestRepository.findById(id).get();

    // String body =
    // leaveServiceImpl.bodyForLeaveStatus(al.getRegister().getFname(), "Approved",
    // al.getLeaveFromDate(), al.getLeaveToDate(), "green");
    // emailServiceImpl.sendEmail(null, al.getRegister().getEmail(), null, null,
    // "Leave Status", body);
    // }

    // @After("rejectLeavePointcut(id)")
    // public void afterLeaveRejection(int id) {

    // Leave al = leaveRequestRepository.findById(id).get();
    // String body =
    // leaveServiceImpl.bodyForLeaveStatus(al.getRegister().getFname(), "Rejected",
    // al.getLeaveFromDate(), al.getLeaveToDate(), "#ff0000");
    // emailServiceImpl.sendEmail(null, al.getRegister().getEmail(), null, null,
    // "Leave Status", body);
    // }
}
