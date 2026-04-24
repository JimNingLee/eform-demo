<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.cabsoft.utils.StringUtils"%>
<%@ page import="com.cabsoft.RXSession"%>
<%@ page errorPage="../../error.jsp"%>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String errUrl = "../error.jsp?msg=";
    if (!request.isRequestedSessionIdValid()) {
        response.sendRedirect(errUrl + java.net.URLEncoder.encode("timeout", "utf-8"));
        return;
    }

    String jobID = (String) session.getAttribute("jobID");
    if (StringUtils.isEmpty(jobID)) {
        response.sendRedirect(errUrl + java.net.URLEncoder.encode("jobID 없음", "utf-8"));
        return;
    }

    RXSession ss = (RXSession) session.getAttribute(jobID + "_session");
    String rptTitle = (ss != null && !StringUtils.isEmpty(ss.getRptFile())) ? ss.getRptFile() : "전자문서";
    String ctxPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title><%= rptTitle %></title>
<style>
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
    background: #525659;
    font-family: Arial, sans-serif;
    font-size: 13px;
    color: #fff;
    overflow-x: hidden;
}

#toolbar {
    position: fixed;
    top: 0; left: 0; right: 0;
    height: 44px;
    background: #323639;
    display: flex;
    align-items: center;
    padding: 0 12px;
    gap: 8px;
    z-index: 100;
    box-shadow: 0 2px 4px rgba(0,0,0,0.4);
}

#toolbar button {
    background: #474b4e;
    border: 1px solid #5a5d61;
    color: #fff;
    padding: 5px 10px;
    cursor: pointer;
    border-radius: 3px;
    font-size: 13px;
}
#toolbar button:hover { background: #5a5d61; }
#toolbar button:disabled { opacity: 0.4; cursor: default; }

#page-info {
    font-size: 13px;
    white-space: nowrap;
}

#title-area {
    flex: 1;
    text-align: center;
    font-size: 14px;
    font-weight: bold;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding: 0 8px;
}

#loading-wrap {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 200;
    flex-direction: column;
    gap: 12px;
}

#loading-wrap.hidden { display: none; }

.spinner {
    width: 40px; height: 40px;
    border: 4px solid #555;
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

#viewer-wrap {
    margin-top: 54px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    min-height: calc(100vh - 54px);
}

.page-canvas {
    display: block;
    box-shadow: 0 2px 8px rgba(0,0,0,0.5);
    background: #fff;
    max-width: 100%;
}

#error-msg {
    display: none;
    background: #c0392b;
    color: #fff;
    padding: 16px 24px;
    border-radius: 4px;
    margin-top: 40px;
    font-size: 14px;
}

@media print {
    #toolbar, #loading-wrap { display: none !important; }
    body { background: #fff; }
    #viewer-wrap { margin-top: 0; padding: 0; gap: 0; }
    .page-canvas {
        display: block;
        width: 100% !important;
        height: auto !important;
        box-shadow: none;
        page-break-after: always;
        page-break-inside: avoid;
    }
}
</style>
</head>
<body>

<div id="loading-wrap">
    <div class="spinner"></div>
    <span>문서를 불러오는 중입니다...</span>
</div>

<div id="toolbar">
    <button id="btn-prev" disabled>◀ 이전</button>
    <span id="page-info">- / -</span>
    <button id="btn-next" disabled>다음 ▶</button>
    <div id="title-area"><%= rptTitle %></div>
    <button id="btn-download">PDF 저장</button>
    <button id="btn-print">인쇄</button>
</div>

<div id="viewer-wrap">
    <div id="error-msg"></div>
</div>

<script src="<%= ctxPath %>/cdoc/eform/homepage/pdfviewer/lib/jquery/jquery-1.12.0.min.js"></script>
<script src="<%= ctxPath %>/cdoc/eform/homepage/pdfviewer/lib/pdf/pdf.js"></script>
<script>
(function() {
    'use strict';

    var CTX_PATH     = '<%= ctxPath %>';
    var JOB_ID       = '<%= jobID %>';
    var DOC_INFO_URL = CTX_PATH + '/cdoc/eform/homepage/pdfviewer/getDocInfo.jsp';
    var DOWNLOAD_URL = CTX_PATH + '/cdoc/eform/homepage/pdfviewer/downloadPdf.jsp';

    var pdfDoc      = null;
    var currentPage = 1;
    var totalPages  = 0;
    var scale       = 1.5;

    pdfjsLib.GlobalWorkerOptions.workerSrc =
        CTX_PATH + '/cdoc/eform/homepage/pdfviewer/lib/pdf/pdf.worker.min.js';

    /* ---------- 로딩 UI ---------- */
    function showLoading(msg) {
        $('#loading-wrap').removeClass('hidden');
    }
    function hideLoading() {
        $('#loading-wrap').addClass('hidden');
    }
    function showError(msg) {
        hideLoading();
        $('#error-msg').text(msg).show();
    }

    /* ---------- 페이지 네비게이션 ---------- */
    function updatePageInfo() {
        $('#page-info').text(currentPage + ' / ' + totalPages);
        $('#btn-prev').prop('disabled', currentPage <= 1);
        $('#btn-next').prop('disabled', currentPage >= totalPages);
    }

    function scrollToPage(n) {
        var canvas = $('#page-canvas-' + n);
        if (canvas.length) {
            $('html, body').animate({ scrollTop: canvas.offset().top - 54 }, 200);
        }
    }

    $('#btn-prev').click(function() {
        if (currentPage > 1) { currentPage--; scrollToPage(currentPage); updatePageInfo(); }
    });
    $('#btn-next').click(function() {
        if (currentPage < totalPages) { currentPage++; scrollToPage(currentPage); updatePageInfo(); }
    });

    /* ---------- 스크롤로 현재 페이지 감지 ---------- */
    $(window).on('scroll', function() {
        for (var n = 1; n <= totalPages; n++) {
            var el = document.getElementById('page-canvas-' + n);
            if (!el) continue;
            var rect = el.getBoundingClientRect();
            if (rect.top <= 100 && rect.bottom > 100) {
                if (currentPage !== n) { currentPage = n; updatePageInfo(); }
                break;
            }
        }
    });

    /* ---------- 페이지 렌더링 ---------- */
    function renderPage(n) {
        return pdfDoc.getPage(n).then(function(page) {
            var viewport = (typeof page.getViewport === 'function' && page.getViewport.length === 0)
                ? page.getViewport({ scale: scale })
                : page.getViewport(scale);

            var canvas = document.createElement('canvas');
            canvas.id = 'page-canvas-' + n;
            canvas.className = 'page-canvas';
            canvas.height = viewport.height;
            canvas.width  = viewport.width;
            document.getElementById('viewer-wrap').appendChild(canvas);

            return page.render({ canvasContext: canvas.getContext('2d'), viewport: viewport }).promise;
        });
    }

    /* ---------- PDF 로드 ---------- */
    function loadPdf(pdfBase64) {
        var pdfData = atob(pdfBase64.replace(/\s+/g, ''));
        pdfjsLib.getDocument({ data: pdfData }).promise.then(function(pdf) {
            pdfDoc      = pdf;
            totalPages  = pdf.numPages;
            currentPage = 1;

            var renders = [];
            for (var n = 1; n <= totalPages; n++) renders.push(renderPage(n));

            Promise.all(renders).then(function() {
                updatePageInfo();
                hideLoading();
            });
        }, function(err) {
            showError('PDF 렌더링 오류: ' + err.message);
        });
    }

    /* ---------- 문서 정보 요청 ---------- */
    function init() {
        $.ajax({
            type: 'POST',
            url: DOC_INFO_URL,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({ jobID: JOB_ID }),
            dataType: 'json',
            success: function(res) {
                if (res.head.cd == 2000) {
                    $('#title-area').text(res.data.title || '<%= rptTitle %>');
                    loadPdf(res.data.pdfEncode);
                } else {
                    showError('문서 로드 실패: ' + (res.head.msg || ''));
                }
            },
            error: function(xhr) {
                showError('서버 통신 오류 (' + xhr.status + ')');
            }
        });
    }

    /* ---------- PDF 저장 ---------- */
    $('#btn-download').click(function() {
        window.location.href = DOWNLOAD_URL + '?jobID=' + encodeURIComponent(JOB_ID);
    });

    /* ---------- 인쇄 ---------- */
    $('#btn-print').click(function() {
        window.print();
    });

    /* ---------- 시작 ---------- */
    $(document).ready(function() {
        init();
    });

})();
</script>
</body>
</html>
