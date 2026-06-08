package com.ape.apeadmin.controller.gen;

import com.ape.apesystem.service.ApeGenTableColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("genColumn")
public class ApeGenTableColumnController {

    @Autowired
    private ApeGenTableColumnService apeGenTableColumnService;



}
