package com.dailyUpdate.pojo;

import lombok.Data;

import java.util.List;

@Data
public class QueryParam {

    private List<List<Param>> queryList;
    private List<ResultsOrderBy> orderByList;

}
