const API_URL = "http://localhost:3434/api/feed/home";

let currentPage = 0;
const pageSize = 10;
let hasNext = true;

async function loadFeeds(page) {
    if (!hasNext && page > 0 ) return;

    const category = document.getElementById("category").value;
    const feedContainer = document.getElementById("feedContainer");
    const loadMoreBtn = document.getElementById("loadMoreBtn");
    const loadingIndicator = document.getElementById("loadingIndicator");

    // loadingIndicator.classList.remove("hidden");

    try {
        const response = await fetch(`${API_URL}?page=${page}&size=${pageSize}&category=${category}`, {
            method: "GET",
            headers: {
                "Authorization" : "Bearer eyJhbGciOiJSUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sInN1YiI6InNqMDEyOTQ0QGdtYWlsLmNvbSIsImlhdCI6MTc0MDk2ODkwNywiZXhwIjoxNzQwOTcyNTA3fQ.CZitD49gMsCi7QVi0eO24NAFJwxgKK8Oo_L0HVSlwCqAFeWptx1R3MT6buVIPvtsxMBurTMeuweQSCf7Pb6DK-o05P4fGJyT1CTTRe2vIPMqWrib939Q3C1eLODJSrrpf7JzrTpQiI1j5XTZWhVUiIotFINn8cQcbi_aMfph9eVCcHtzW7nexmblmwJOb6iJ0aA0KKTjmX7pA4sn_UbPbU-tWrefpbM02eaHDbb3texBjBOBGIdjtQM_XOGCiQDKSvadgQV5E_lQ4JH1LnwFWGRo4Xrw2JVTX9BIf1sA80envsJz-Bq0GtrNp-PlJgxYHSPOcllsapbqTpVxbRIybQ",
                "Content-Type" : "application/json"
            },
        });

        if (!response.ok) throw new Error("API 요청 실패");

        const data = await response.json();
        console.log(data);
        hasNext = data.hasNext;

        data.list.forEach(feed =>  {
            const listItem = document.createElement("lki");
            listItem.className = "feed-item";

            let imageHTML = "";
            if (feed.images .length > 0) {
                imageHTML = feed.images.map(imgUrl => `<img src="${imgUrl}" class="feed-image">`);
            }    

            listItem.innerHTML = `<h3>${feed.nickname} | ${feed.category}<h3>
                             <p>${feed.content || "내용 없음"}</p>
                             ${imageHTML}
                             <div class="feed-footer">
                                <span class="feed-date">${feed.datetime}</span>
                                <button class="like-btn" data-liked="${feed.like}>${feed.like ? "♥ 좋아요" : "♡좋아요"}</button>
                             </div>`

            const likeBtn = listItem.querySelector(".like-btn");
            likeBtn.addEventListener("click", () => toggleLike(likeBtn));

            feedContainer.appendChild(listItem);

        });
        currentPage = page;

        loadMoreBtn.style.display = hasNext ? "block" : "none";
    } catch (error) {
        console.error("피드 불러오기 오류:", error);
    }
}

function toggleLike(button) {
    const isLiked = button.getAttribute("data-liked") === "true";
    button.innerHTML = isLiked ? "♥ 좋아요" : "♡좋아요";
    button.setAttribute("data-liked", isLiked ? "false" : "true");
}

function loadMoreFeeds() {
    loadFeeds(currentPage + 1);
}