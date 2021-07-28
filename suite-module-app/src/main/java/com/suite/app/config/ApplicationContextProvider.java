package com.suite.app.config;

// VALIDAR SE EH NECESSARIO

// package com.solution.core.config;
//
// import org.springframework.beans.BeansException;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.ApplicationContextAware;
// import org.springframework.stereotype.Component;
//
// @Component
// public class ApplicationContextProvider implements ApplicationContextAware {
//
// private ApplicationContext applicationContext;
//
// @Override
// public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
// this.applicationContext = applicationContext;
// }
//
// public ApplicationContext getContext() {
// return applicationContext;
// }
//
// public <T> T getBean(Class<T> type) {
// if (applicationContext != null) {
// return applicationContext.getBean(type);
// } else {
// throw new RuntimeException("The aaplication context is null");
// }
// }
//
// }