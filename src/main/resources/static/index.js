// 중첩 경로를 안전하게 조회
function getPathOrDefault(object, path, fallback = undefined) {
    try {
        const value = path
            .split(".")
            .reduce((accumulator, key) => (accumulator && key in accumulator ? accumulator[key] : undefined), object);
        return value ?? fallback;
    } catch {
        return fallback;
    }
}

// 비율(%) 계산
function percentage(numerator, denominator) {
    if (numerator === undefined || denominator === undefined || denominator === 0) return 0;
    const value = Math.round((numerator / denominator) * 100);
    return Math.max(0, Math.min(100, value));
}

// API 호출: /ask?userMessage=...
async function callApi(userMessage) {
    const url = `/ask?userMessage=${encodeURIComponent(userMessage)}`;
    const response = await fetch(url, { method: "GET" });

    if (!response.ok) {
        const text = await response.text().catch(() => "");
        throw new Error(`요청 실패(${response.status}) ${text || ""}`.trim());
    }
    // 서버는 Spring AI ChatResponse 포맷 JSON을 반환해야 합니다.
    return await response.json();
}

// UI 갱신 (jQuery 사용)
function updateUI(chatResponse) {
    // 메인 응답
    const messageText = getPathOrDefault(chatResponse, "result.output.text", "");
    $("#assistantText").text(messageText || "알 수 없습니다");

    // 모델/ID
    const modelName = getPathOrDefault(chatResponse, "metadata.model", "-");
    const responseId = getPathOrDefault(chatResponse, "metadata.id", "");

    $("#modelName").text(modelName || "-");
    const $idElement = $("#responseId");
    $idElement.text(responseId || "-");
    $idElement.attr("title", responseId || "");

    // Rate Limit
    const rateLimit = getPathOrDefault(chatResponse, "metadata.rateLimit", {});
    const requestsLimit = rateLimit.requestsLimit;
    const requestsRemaining = rateLimit.requestsRemaining;
    const tokensLimit = rateLimit.tokensLimit;
    const tokensRemaining = rateLimit.tokensRemaining;

    const requestsPercentage = percentage(requestsRemaining, requestsLimit);
    const tokensPercentage = percentage(tokensRemaining, tokensLimit);

    $("#requestsStat").text(`${requestsRemaining ?? "-"} / ${requestsLimit ?? "-"}`);
    $("#requestsBar").css("width", `${requestsPercentage}%`);
    $("#requestsReset").text(rateLimit.requestsReset ? `reset: ${rateLimit.requestsReset}` : "");

    $("#tokensStat").text(`${tokensRemaining ?? "-"} / ${tokensLimit ?? "-"}`);
    $("#tokensBar").css("width", `${tokensPercentage}%`);
    $("#tokensReset").text(rateLimit.tokensReset ? `reset: ${rateLimit.tokensReset}` : "");

    // 사용량
    const usage = getPathOrDefault(chatResponse, "metadata.usage", {});
    $("#promptTokens").text(usage.promptTokens ?? "-");
    $("#completionTokens").text(usage.completionTokens ?? "-");
    $("#totalTokens").text(usage.totalTokens ?? "-");
}

// ========== Embedding 탭 관련 함수 ==========

// 텍스트 truncate 함수
function truncateText(text, maxLength = 50) {
    if (!text) return '-';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Documents 로드 함수
async function loadDocuments() {
    try {
        // TODO: 실제 API 호출로 변경
        // const response = await fetch('/api/documents');
        // const documents = await response.json();

        // 샘플 데이터로 테이블 렌더링
        renderDocumentsTable([]);
    } catch (error) {
        console.error('Documents 로드 실패:', error);
    }
}

// Documents 테이블 렌더링
function renderDocumentsTable(documents) {
    const $tbody = $('#documentsTableBody');
    const $noDataMsg = $('#noDocumentsMessage');

    if (documents && documents.length > 0) {
        $tbody.show();
        $noDataMsg.addClass('hidden');

        const rows = documents.map(doc => `
            <tr class="border-b border-slate-100 hover:bg-slate-50 cursor-pointer">
                <td class="px-3 py-3 text-slate-900 font-mono text-xs">${doc.id}</td>
                <td class="px-3 py-3 text-slate-600 font-mono text-xs">
                    <span class="truncate-text">${truncateText(JSON.stringify(doc.vector), 30)}</span>
                </td>
                <td class="px-3 py-3 text-slate-600">
                    <span class="truncate-text">${truncateText(doc.content)}</span>
                </td>
                <td class="px-3 py-3 text-slate-600 font-mono text-xs">
                    <span class="truncate-text">${truncateText(JSON.stringify(doc.metadata), 30)}</span>
                </td>
                <td class="px-3 py-3 text-center">
                    <button class="view-doc-btn text-slate-600 hover:text-slate-900" data-doc-id="${doc.id}">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                        </svg>
                    </button>
                </td>
            </tr>
        `).join('');

        $tbody.html(rows);
    } else {
        $tbody.hide();
        $noDataMsg.removeClass('hidden');
    }
}

// Document 상세정보 모달 표시
function showDocumentModal(docData) {
    $('#modalDocId').text(docData.id || '-');
    $('#modalDocVector').text(docData.vector || '-');
    $('#modalDocContent').text(docData.content || '-');

    // metadata를 JSON 형식으로 예쁘게 표시
    if (docData.metadata) {
        try {
            const metadataObj = typeof docData.metadata === 'string'
                ? JSON.parse(docData.metadata)
                : docData.metadata;
            $('#modalDocMetadata').text(JSON.stringify(metadataObj, null, 2));
        } catch {
            $('#modalDocMetadata').text(docData.metadata);
        }
    } else {
        $('#modalDocMetadata').text('-');
    }

    $('#documentModal').addClass('show');
}

// Document 상세정보 모달 닫기
function closeDocumentModal() {
    $('#documentModal').removeClass('show');
}