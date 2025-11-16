$(function () {
    const $endpointInput = $('#endpointInput');
    const $questionInput = $('#questionInput');
    const $sendButton = $('#sendButton');
    const $resultArea = $('#resultArea');
    const $apiChat = $('input[name="apiType"]');

    function setLoading(isLoading) {
        if (isLoading) {
            $sendButton.prop('disabled', true);
            $sendButton.text('전송 중...');
        } else {
            $sendButton.prop('disabled', false);
            $sendButton.text('전송');
        }
    }

    function sendRequest() {
        const endpoint = $.trim($endpointInput.val());
        const question = $.trim($questionInput.val());

        if (!endpoint) {
            alert('Endpoint를 입력해 주세요.');
            return;
        }
        if (!question) {
            alert('Question을 입력해 주세요.');
            return;
        }

        $resultArea.val('요청 중입니다...');
        setLoading(true);

        $.ajax({
            url: endpoint,
            method: 'GET',
            dataType: 'text', // 응답을 그대로 TextArea에 넣기 위함
            data: {
                question: question   // → /endpoint?question=xxxx 형태로 전송됨
            }
        })
        .done((data) => {
            $resultArea.val(data);
            setLoading(false);
        })
        .fail((jqXHR, textStatus, errorThrown) => {
            let msg = '요청 중 오류가 발생했습니다.\n\n';
            msg += 'Status: ' + (jqXHR.status || 'N/A') + ' ' + (jqXHR.statusText || '') + '\n';
            msg += 'Error: ' + (errorThrown || textStatus);
            $resultArea.val(msg);
            setLoading(false);
        })
    }

    $sendButton.on('click', () => {
        sendRequest();
    });

    // Question 입력창에서 Enter로 전송
    $questionInput.on('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            sendRequest();
        }
    });

    $apiChat.on('change', (event) => {
        $endpointInput.val(event.target.value);
    });
});
