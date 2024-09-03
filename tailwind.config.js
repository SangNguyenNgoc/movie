/** @type {import("tailwindcss").Config} */
module.exports = {
  content: ["./src/main/resources/templates/**/*.{html,js}"],
  theme: {
    extend: {
      backgroundImage: {
        loginBackground: "url('/images/background.png')",
        iconFacebook: "url('/images/IconFacebook.png')",
        iconGoogle: "url('/images/IconGoogle.png')",
        iconTwitter: "url('/images/IconTwitter.png')",
        notFound: "url('/images/error.svg')"
      },
      colors: {
        primary: "#31D7A9",
        notice: "#31AFD7",
        formBackground: "rgba(5, 17, 63, 0.8)",
        errorBackground: "rgb(9, 25, 54)",
        label: "rgba(255,255,255,0.7)",
        placeHolder: "rgba(255,255,255,0.2)"
      },
      fontFamily: {
        comfortaa: ["Comfortaa", "sans-serif"],
        inter: ["Inter", "sans-serif"]
      }
    }
  },
  plugins: []
};

