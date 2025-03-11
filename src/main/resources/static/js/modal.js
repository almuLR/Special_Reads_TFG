document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("readingProgressModal");
    const closeModal = document.querySelector(".close");
    const covers = document.querySelectorAll(".cover");
    const progressInput = document.getElementById("progressInput");

    covers.forEach(cover => {
        cover.addEventListener("click", function () {
            const bookId = this.getAttribute("data-book-id");
            const title = this.getAttribute("data-title");
            const progress = this.getAttribute("data-progress");

            progressInput.value = progress + "%";

            modal.style.display = "block";
        });
    });

    closeModal.addEventListener("click", function () {
        modal.style.display = "none";
    });

    window.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    });
    // Escuchar cambios en el select
    progressType.addEventListener("change", function () {
        if (progressType.value === "pages") {
            progressInput.placeholder = "";
        } else {
            progressInput.placeholder = "";
        }
    });
});

