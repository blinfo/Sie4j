module Sie4j {
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires org.apache.commons.codec;
    exports sie;
    exports sie.domain;
    exports sie.dto;
    exports sie.exception;
    exports sie.log;
    exports sie.io to com.fasterxml.jackson.databind;
}
