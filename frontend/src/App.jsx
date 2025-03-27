// import {Route, Routes} from "react-router-dom";
import Layout from "./components/layout/Layout.jsx";
import HeaderTest from "./components/common/HeaderTest.jsx";
import React from "react";


function App() {

    return (
        <>
            <div>
                <header className={"mx-6 p-4 flex items-center justify-between"}>
                    <a href="" className={"flex items-center gap-1"}>
                        <svg className="block h-8" aria-hidden="true" viewBox="0 0 32 32"
                             xmlns="http://www.w3.org/2000/svg" fill="#FF385C">
                            <path
                                d="M16 1c2.008 0 3.463.963 4.751 3.269l.533 1.025c1.954 3.83 6.114 12.54 7.1 14.836l.145.353c.667 1.591.91 2.472.96 3.396l.01.415.001.228c0 4.062-2.877 6.478-6.357 6.478-2.224 0-4.556-1.258-6.709-3.386l-.257-.26-.172-.179h-.011l-.176.185c-2.044 2.1-4.267 3.42-6.414 3.615l-.28.019-.267.006C5.377 31 2.5 28.584 2.5 24.522l.005-.469c.026-.928.23-1.768.83-3.244l.216-.524c.966-2.298 6.083-12.989 7.707-16.034C12.537 1.963 13.992 1 16 1zm0 2c-1.239 0-2.053.539-2.987 2.21l-.523 1.008c-1.926 3.776-6.06 12.43-7.031 14.692l-.345.836c-.427 1.071-.573 1.655-.605 2.24l-.009.33v.206C4.5 27.395 6.411 29 8.857 29c1.773 0 3.87-1.236 5.831-3.354-2.295-2.938-3.855-6.45-3.855-8.91 0-2.913 1.933-5.386 5.178-5.386 3.245 0 5.178 2.473 5.178 5.385 0 2.463-1.561 5.977-3.857 8.907 2.02 2.123 4.115 3.358 5.869 3.358 2.447 0 4.358-1.605 4.358-4.478l-.004-.411c-.019-.672-.17-1.296-.714-2.62l-.248-.6c-1.065-2.478-5.993-12.768-7.538-15.664C18.053 3.539 17.24 3 16 3zm.01 10.316c-2.01 0-3.177 1.514-3.177 3.384 0 1.735 1.154 4.53 3.065 7.058 2.025-2.543 3.178-5.334 3.178-7.058 0-1.87-1.168-3.384-3.178-3.384z"/>
                        </svg>
                        <span className={"font-bold text-xl"}>Airbnb</span>
                    </a>

                    {/*Search*/}
                    <div className={"flex gap-4 items-center border border-gray-200 rounded-full py-2 px-4 shadow-sm shadow-gray-300"}>
                        <div className={"border-r border-gray-300 px-4"}>
                            Anywhere
                        </div>
                        <div className={"border-r border-gray-300 px-4"}>Any week</div>
                        <div className={"border-r border-gray-300 px-4"}>Add guest</div>
                        <button className={"flex bg-primary text-white p-2 rounded-full"}>
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5}
                                 stroke="currentColor" className="size-4">
                                <path strokeLinecap="round" strokeLinejoin="round"
                                      d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z"/>
                            </svg>
                        </button>
                    </div>
                    {/*Profile*/}
                    <div className={"flex items-center border-r border-gray-300 px-4 py-2 rounded-full shadow-sm shadow-gray-300 shadow-gray-300"}>
                        <button>
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5}
                                 stroke="currentColor" className="size-6">
                                <path strokeLinecap="round" strokeLinejoin="round"
                                      d="M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
                            </svg>
                        </button>

                    </div>
                </header>
            </div>
        </>


    )
}

export default App
