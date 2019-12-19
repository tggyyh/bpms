//@ sourceURL=pagination.js
function initPagination(totalRows,pageNumber,pageSize) {
    var html = [],
        $allSelected = false,
        i, from, to,
        $pageList,
        $first, $pre,
        $next, $last,
        $number,
        pageFrom,
        pageTo,
        pageList = [5, 10, 25, 50],
        totalPages = 0;
    totalPages=(parseInt((totalRows - 1) / pageSize)) + 1;
    pageFrom = (pageNumber - 1) * pageSize + 1;
    pageTo = pageNumber * pageSize;
    if(pageTo>totalRows){
        pageTo = totalRows;
    }
    html.push('<div class="pull-left pagination-detail">',
              '<span class="pagination-info">',
              '<span class="pagination-info">显示第 '+pageFrom +'到第'+ pageTo+' 条记录，总共'+ totalRows+' 条记录</span>' ,
                '<span class="page-list">每页显示 <span class="btn-group dropup">',
                '<button type="button" class="btn btn-default  dropdown-toggle" data-toggle="dropdown">',
                 '<span class="page-size">',
                    pageSize,
                    '</span>',
                ' <span class="caret"></span>',
                '</button>',
                '<ul class="dropdown-menu" role="menu">');

    $.each(pageList,function(i,page){
            var active;
            active = page === pageSize ? ' class="active"' : '';
            html.push(sprintf('<li%s><a href="javascript:void(0)">%s</a></li>', active, page));
    });
    html.push('</ul></span>');
    html.push('条记录');
    html.push('</span> </div>');
    html.push('<div class="pull-right pagination">',
        '<ul class="pagination">',
        '<li class="page-pre"><a href="javascript:void(0)">‹</a></li>');
    if (totalPages < 5) {
        from = 1;
        to = totalPages;
    } else {
        from = pageNumber - 2;
        to = from + 4;
        if (from < 1) {
            from = 1;
            to = 5;
        }
        if (to > totalPages) {
            to = totalPages;
            from = to - 4;
        }
    }

    if (totalPages >= 6) {
        if (pageNumber >= 3) {
            html.push('<li class="page-first' + (1 === pageNumber ? ' active' : '') + '">',
                '<a href="javascript:void(0)">', 1, '</a>',
                '</li>');

            from++;
        }

        if (pageNumber >= 4) {
            if (pageNumber == 4 || totalPages == 6 || totalPages == 7) {
                from--;
            } else {
                html.push('<li class="page-first-separator disabled">',
                    '<a href="javascript:void(0)">...</a>',
                    '</li>');
            }

            to--;
        }
    }

    if (totalPages >= 7) {
        if (pageNumber >= (totalPages - 2)) {
            from--;
        }
    }

    if (totalPages == 6) {
        if (pageNumber >= (totalPages - 2)) {
            to++;
        }
    } else if (totalPages >= 7) {
        if (totalPages == 7 || pageNumber >= (totalPages - 3)) {
            to++;
        }
    }

    for (i = from; i <= to; i++) {
        html.push('<li class="page-number' + (i === pageNumber ? ' active' : '') + '">',
            '<a href="javascript:void(0)">', i, '</a>',
            '</li>');
    }

    if (totalPages >= 8) {
        if (pageNumber <= (totalPages - 4)) {
            html.push('<li class="page-last-separator disabled">',
                '<a href="javascript:void(0)">...</a>',
                '</li>');
        }
    }

    if (totalPages >= 6) {
        if (pageNumber <= (totalPages - 3)) {
            html.push('<li class="page-last' + (totalPages === pageNumber ? ' active' : '') + '">',
                '<a href="javascript:void(0)">', totalPages, '</a>',
                '</li>');
        }
    }

    html.push(
        '<li class="page-next"><a href="javascript:void(0)">›</a></li>',
        '</ul>',
        '</div>');
    $('.fixed-table-pagination').html(html.join(''));

    $('.page-list a').off('click').on('click', function(event){
        var $this = $(event.currentTarget);
        $this.parent().addClass('active').siblings().removeClass('active');
        var currentPageSize = $this.text();
        $('.page-size').text(currentPageSize);
        updatePagination(currentPageSize,pageNumber);
    });
    $('.page-first').off('click').on('click', function(event){
        updatePagination(pageSize,1);
    });
    $('.page-pre').off('click').on('click', function(event){
        updatePagination(pageSize,pageNumber-1);
    });
    $('.page-next').off('click').on('click', function(event){
        updatePagination(pageSize,pageNumber+1);
    });
    $('.page-last').off('click').on('click', function(event){
        updatePagination(pageSize,totalPages);
    });
    $('.page-number').off('click').on('click', function(event){
        if (pageNumber === +$(event.currentTarget).text()) {
            return;
        }
        pageNumber = +$(event.currentTarget).text();
        updatePagination(pageSize,pageNumber);
    });
}

function sprintf(str) {
    var args = arguments,
         flag = true,
        i = 1;

    str = str.replace(/%s/g, function () {
        var arg = args[i++];

        if (typeof arg === 'undefined') {
            flag = false;
            return '';
        }
        return arg;
    });
    return flag ? str : '';
};

//table查询参数
function queryCommonParams() {  //配置参数
    var type="";
    if($("#drop_search_listedPlace").val()!=""||
        $("#drop_search_type").val()!="" ||
        $("#txt_search_valueDate_begin").val()!="" ||
        $("#txt_search_valueDate_end").val()!="" ||
        $("#txt_search_payDay_begin").val()!="" ||
        $("#txt_search_payDay_end").val()!="" ||
        $("#txt_search_timeLimit_begin").val()!="" ||
        $("#txt_search_timeLimit_end").val()!="" ||
        $("#drop_search_user").val()!="" ||
        $("#txt_search_guaranteeCompany").val()!="" ||
        $("#drop_search_bondManagerType").val()!="" ||
        $("#txt_search_bondManager").val()!="" ||
        $("#search_matter_type").val()=="1"
    ){
        type="1";
    }
    if($("#search_matter_type").val()=="0"  ){
        type="0";
    }
    return {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
        pageSize: limit,
        offset: offset,
        common: $("#search_common").val(),
        type: type,
        completeDate: $("#search_completeDate").val(),
        listedPlace: $("#drop_search_listedPlace").val(),
        bondType: $("#drop_search_type").val(),
        province: $("#drop_search_province").val(),
        city: $("#drop_search_city").val(),
        valueDateBegin: $("#txt_search_valueDate_begin").val(),
        valueDateEnd: $("#txt_search_valueDate_end").val(),
        payDayBegin: $("#txt_search_payDay_begin").val().replace("年",""),
        payDayEnd: $("#txt_search_payDay_end").val().replace("年",""),
        timeLimitBegin: Number($("#txt_search_timeLimit_begin").val()),
        timeLimitEnd: Number($("#txt_search_timeLimit_end").val()),
        user: $("#drop_search_user").val(),
        guaranteeCompany: $("#txt_search_guaranteeCompany").val(),
        bondManagerType: $("#drop_search_bondManagerType").val(),
        matterName: $("#txt_search_matterName").val(),
        bondManager: $("#txt_search_bondManager").val()
    };
}