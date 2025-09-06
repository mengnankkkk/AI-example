// voiceprint.js
// 声纹处理前端交互脚本
// 依赖：jQuery、Bootstrap

let mediaRecorder;
let audioChunks = [];
let audioBlob;
let audioBase64 = '';

// 录音相关
function startRecording() {
    audioChunks = [];
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(stream => {
            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.start();
            mediaRecorder.ondataavailable = e => audioChunks.push(e.data);
            mediaRecorder.onstop = () => {
                audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
                blobToBase64(audioBlob, base64 => {
                    audioBase64 = base64;
                    $("#audioPreview").attr('src', URL.createObjectURL(audioBlob));
                });
            };
        })
        .catch(err => alert('无法访问麦克风: ' + err));
}

function stopRecording() {
    if (mediaRecorder) mediaRecorder.stop();
}

function blobToBase64(blob, callback) {
    const reader = new FileReader();
    reader.onloadend = () => callback(reader.result.split(',')[1]);
    reader.readAsDataURL(blob);
}

// 声纹注册
function enrollVoiceprint() {
    const userId = $("#enrollUserId").val();
    if (!userId || !audioBase64) {
        alert('请填写用户ID并录音');
        return;
    }
    $.ajax({
        url: '/api/voiceprint/enroll',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ userId, audioBase64 }),
        success: res => {
            alert('注册成功: ' + res.message);
            loadUserVoiceprints();
        },
        error: xhr => alert('注册失败: ' + xhr.responseText)
    });
}

// 声纹识别
function identifyVoiceprint() {
    if (!audioBase64) {
        alert('请先录音');
        return;
    }
    $.ajax({
        url: '/api/voiceprint/identify',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ audioBase64 }),
        success: res => {
            $("#identifyResult").text('识别结果: ' + res.userId + (res.score ? ' (分数: ' + res.score + ')' : ''));
            loadIdentificationLogs();
        },
        error: xhr => alert('识别失败: ' + xhr.responseText)
    });
}

// 查询用户声纹
function loadUserVoiceprints() {
    const userId = $("#enrollUserId").val();
    if (!userId) return;
    $.get('/api/voiceprint/user/' + userId, res => {
        const list = res || [];
        let html = '';
        list.forEach(vp => {
            html += `<tr><td>${vp.id}</td><td>${vp.createTime}</td><td><button class="btn btn-danger btn-sm" onclick="deleteVoiceprint(${vp.id})">删除</button></td></tr>`;
        });
        $("#voiceprintList").html(html);
    });
}

// 删除声纹
function deleteVoiceprint(id) {
    if (!confirm('确定删除该声纹？')) return;
    $.ajax({
        url: '/api/voiceprint/' + id,
        method: 'DELETE',
        success: () => {
            alert('删除成功');
            loadUserVoiceprints();
        },
        error: xhr => alert('删除失败: ' + xhr.responseText)
    });
}

// 查询识别日志
function loadIdentificationLogs() {
    $.get('/api/voiceprint/logs', res => {
        let html = '';
        (res || []).forEach(log => {
            html += `<tr><td>${log.id}</td><td>${log.userId}</td><td>${log.score}</td><td>${log.createTime}</td></tr>`;
        });
        $("#logList").html(html);
    });
}

// 查询统计
function loadVoiceprintStats() {
    $.get('/api/voiceprint/stats', res => {
        $("#statsTotal").text(res.total || 0);
        $("#statsToday").text(res.today || 0);
    });
}

// 事件绑定
$(function() {
    $("#btnStartRecord").click(startRecording);
    $("#btnStopRecord").click(stopRecording);
    $("#btnEnroll").click(enrollVoiceprint);
    $("#btnIdentify").click(identifyVoiceprint);
    $("#enrollUserId").change(loadUserVoiceprints);
    loadVoiceprintStats();
    loadIdentificationLogs();
});
