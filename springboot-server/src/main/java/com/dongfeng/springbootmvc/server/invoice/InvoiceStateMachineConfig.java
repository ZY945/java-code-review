package com.dongfeng.springbootmvc.server.invoice;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class InvoiceStateMachineConfig extends StateMachineConfigurerAdapter<InvoiceState, InvoiceEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<InvoiceState, InvoiceEvent> states) throws Exception {
        states
            .withStates()
                .initial(InvoiceState.INITIAL)  // 初始状态
                .states(EnumSet.allOf(InvoiceState.class)); // 所有状态
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<InvoiceState, InvoiceEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(InvoiceState.INITIAL).target(InvoiceState.APPLIED).event(InvoiceEvent.SUBMIT) // 提交申请
            .and()
            .withExternal()
                .source(InvoiceState.APPLIED).target(InvoiceState.ISSUED).event(InvoiceEvent.APPROVE) // 申请通过
            .and()
            .withExternal()
                .source(InvoiceState.ISSUED).target(InvoiceState.CANCELLED).event(InvoiceEvent.CANCEL) // 红冲
            .and()
            .withExternal()
                .source(InvoiceState.CANCELLED).target(InvoiceState.ISSUED).event(InvoiceEvent.REISSUE); // 重开票
    }
}