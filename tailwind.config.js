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
        glxCustom: "#034ea2",
        vipSeat: "rgb(147,73,8)",
        hallPrimary: "rgb(3,16,28,1)",
        primary800: "rgb(5,23,43)",
        primary950: "#030E19",
        primary900: "#061D33",
        textPrimary: "#58C9FC",
        primary700: "rgba(18, 86, 153, 1)",
        primary500: "#0CAAF2",
        primary1000: "#020A13",
        searchText: "#0F172A",
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

