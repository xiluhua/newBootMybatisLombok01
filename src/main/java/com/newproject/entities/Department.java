package com.newproject.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department {

    private Integer id;
    private String departmentName;

    public static void main(String[] args) {
        DepartmentBuilder builder = new DepartmentBuilder();
        builder.departmentName("销售部")
                .id(1000);
        System.out.println(builder.toString());
    }
}
