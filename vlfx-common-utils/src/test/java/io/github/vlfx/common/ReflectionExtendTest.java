package io.github.vlfx.common;

/**
 * @author vLfx
 * @date 2026/5/16 22:43
 */
public class ReflectionExtendTest {

    public static class JavaSourceClass{
        private String name;
        private Integer age;
        private String email;

        public JavaSourceClass() {
        }

        public JavaSourceClass(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class JavaTargetClass{
        private String name;
        private Integer age;
        private String phone;
        private String email;
//
//        public JavaTargetClass() {
//        }
//
//        public JavaTargetClass(String name, Integer age, String phone, String email) {
//            this.name = name;
//            this.age = age;
//            this.phone = phone;
//            this.email = email;
//        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
