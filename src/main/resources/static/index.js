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
