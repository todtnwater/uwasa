// ===== 다크/라이트 모드 =====
function toggleTheme() {
    const body = document.body;
    const btn = document.querySelector('.theme-btn');
    if (body.getAttribute('data-theme') === 'dark') {
        body.removeAttribute('data-theme');
        localStorage.setItem('theme', 'light');
        btn.textContent = '🌙';
    } else {
        body.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
        btn.textContent = '☀️';
    }
}

(function () {
    const saved = localStorage.getItem('theme');
    const btn = document.querySelector('.theme-btn');
    if (saved === 'dark') {
        document.body.setAttribute('data-theme', 'dark');
        if (btn) btn.textContent = '☀️';
    }
})();

// ===== 공통 유틸 =====
function escapeHtml(str) {
    if (!str) return '';
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${month}/${day} ${hours}:${minutes}`;
}

function showToast(msg) {
    const toast = document.createElement('div');
    toast.textContent = msg;
    toast.style.cssText = `
        position: fixed;
        bottom: 2rem;
        left: 50%;
        transform: translateX(-50%);
        background: #333;
        color: #fff;
        padding: 0.8rem 1.5rem;
        border-radius: 6px;
        font-size: 0.9rem;
        z-index: 9999;
        opacity: 0;
        transition: opacity 0.3s;
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.style.opacity = '1', 50);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ===== SSE 실시간 =====
const postList = document.getElementById('postList');

if (postList) {
    const sse = new EventSource('/sse/subscribe');

    // 소멸
    sse.addEventListener('consumed', function (e) {
        const postId = e.data;
        const card = postList.querySelector(`.post-card[data-id="${postId}"]`);
        if (card) {
            const animations = [
                { animation: 'dissolveLeft 0.8s ease forwards' },
                { animation: 'dissolveUp 0.6s ease forwards' },
                { animation: 'dissolveBurn 0.7s ease forwards' },
                { animation: 'dissolveAsh 0.9s ease forwards' },
                { animation: 'dissolveFade 0.5s ease forwards' },
            ];
            const effect = Math.random() < 0.5
                ? animations[0]
                : animations[Math.floor(Math.random() * animations.length)];
            card.style.animation = effect.animation;
            setTimeout(() => {
                card.remove();
                if (postList.querySelectorAll('.post-card').length === 0) {
                    postList.innerHTML = '<div class="empty-msg">아직 소문이 없습니다. 첫 번째 소문을 남겨보세요!</div>';
                }
            }, 900);
        }
    });

    // 생성
    sse.addEventListener('created', function (e) {
        const parts = e.data.split('|');
        const postId = parts[0];
        const title = parts[1];
        const writerNickname = parts[2];
        const createdAt = parts[3];

        if (postList.querySelector(`.post-card[data-id="${postId}"]`)) return;

        const emptyMsg = postList.querySelector('.empty-msg');
        if (emptyMsg) emptyMsg.remove();

        const appearAnimations = [
            'appearRight 0.8s ease forwards',
            'appearDown 0.6s ease forwards',
            'appearGlow 0.7s ease forwards',
            'appearPop 0.5s ease forwards',
            'appearFade 0.5s ease forwards',
        ];
        const randomAppear = appearAnimations[Math.floor(Math.random() * appearAnimations.length)];

        const card = document.createElement('div');
        card.className = 'post-card';
        card.setAttribute('data-id', postId);
        card.style.animation = randomAppear;
        card.innerHTML = `
            <div class="post-header">
                <span class="post-nickname">${escapeHtml(writerNickname)}</span>
                <span class="post-time">${formatDate(createdAt)}</span>
            </div>
            <div class="post-title">${escapeHtml(title)}</div>
            <div class="post-footer">
                <form action="/post/read/${postId}" method="post">
                    <button type="submit" class="read-btn">👁️ 읽기 (소멸됩니다)</button>
                </form>
            </div>
        `;
        postList.appendChild(card);
    });

    // 배너 통계 갱신
    sse.addEventListener('stats', function (e) {
        const parts = e.data.split('|');
        const consumedToday = parts[0];
        const alive = parts[1];
        const totalConsumed = parts[2];
        const bannerItems = document.querySelectorAll('.banner-item span');
        if (bannerItems.length >= 3) {
            bannerItems[0].textContent = consumedToday;
            bannerItems[1].textContent = alive;
            bannerItems[2].textContent = totalConsumed;
        }
    });

    sse.onerror = function () {
        sse.close();
    };
}

// ===== 소멸/밴 알림 토스트 =====
(function () {
    const params = new URLSearchParams(window.location.search);
    if (params.get('consumed') === 'true') {
        showToast('이미 소멸된 소문입니다.');
        history.replaceState(null, '', '/');
    }
    if (params.get('banned') === 'true') {
        showToast('접근이 제한된 사용자입니다.');
        history.replaceState(null, '', '/');
    }
})();

// ===== 신고 모달 =====
function showReportModal() {
    document.getElementById('reportModal').style.display = 'flex';
}

function hideReportModal() {
    document.getElementById('reportModal').style.display = 'none';
}

function submitReport() {
    const selected = document.querySelector('input[name="reason"]:checked');
    if (!selected) {
        alert('신고 사유를 선택해주세요.');
        return;
    }
    const detail = document.getElementById('reportDetail').value;
    const reason = selected.value + (detail ? ' - ' + detail : '');
    document.getElementById('reportReasonInput').value = reason;

    const form = document.getElementById('reactionForm');
    const hiddenEmoji = document.createElement('input');
    hiddenEmoji.type = 'hidden';
    hiddenEmoji.name = 'emoji';
    hiddenEmoji.value = '🚨';
    form.appendChild(hiddenEmoji);

    hideReportModal();
    form.submit();
}

// ===== 신고 목록 무한스크롤 (관리자) =====
function renderReport(report) {
    const div = document.createElement('div');
    div.className = 'ban-card';
    div.innerHTML = `
        <div class="ban-info">
            <span class="ban-ip">IP: ${escapeHtml(report.ip)}</span>
            <span class="ban-reason">사유: ${escapeHtml(report.reportReason || '사유 없음')}</span>
            <span class="ban-expired">신고 시간: ${formatDate(report.createdAt)}</span>
        </div>
        <form action="/admin/ban" method="post">
            <input type="hidden" name="ip" value="${escapeHtml(report.ip)}">
            <input type="hidden" name="reason" value="${escapeHtml(report.reportReason || '')}">
            <button type="submit" class="btn">IP 밴</button>
        </form>
    `;
    return div;
}

function disableSubmit(form) {
    const btn = form.querySelector('button[type="submit"]');
    if (btn) {
        btn.disabled = true;
        btn.textContent = '전송 중...';
    }
}