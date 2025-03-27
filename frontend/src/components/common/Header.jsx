import React from 'react';
import { Search, Globe, Menu, User } from 'lucide-react';

const Header = () => {
    return (
        <header className="fixed w-full bg-white mx-4 ">
            <div className="py-4 ">
                <div className="max-w-[2520px] mx-auto px-4 sm:px-6">
                    <div className="flex items-center justify-between">
                        {/* Logo */}
                        <div className="flex-shrink-0">
                            <svg className="block h-8" aria-hidden="true" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg" fill="#FF385C">
                                <path d="M16 1c2.008 0 3.463.963 4.751 3.269l.533 1.025c1.954 3.83 6.114 12.54 7.1 14.836l.145.353c.667 1.591.91 2.472.96 3.396l.01.415.001.228c0 4.062-2.877 6.478-6.357 6.478-2.224 0-4.556-1.258-6.709-3.386l-.257-.26-.172-.179h-.011l-.176.185c-2.044 2.1-4.267 3.42-6.414 3.615l-.28.019-.267.006C5.377 31 2.5 28.584 2.5 24.522l.005-.469c.026-.928.23-1.768.83-3.244l.216-.524c.966-2.298 6.083-12.989 7.707-16.034C12.537 1.963 13.992 1 16 1zm0 2c-1.239 0-2.053.539-2.987 2.21l-.523 1.008c-1.926 3.776-6.06 12.43-7.031 14.692l-.345.836c-.427 1.071-.573 1.655-.605 2.24l-.009.33v.206C4.5 27.395 6.411 29 8.857 29c1.773 0 3.87-1.236 5.831-3.354-2.295-2.938-3.855-6.45-3.855-8.91 0-2.913 1.933-5.386 5.178-5.386 3.245 0 5.178 2.473 5.178 5.385 0 2.463-1.561 5.977-3.857 8.907 2.02 2.123 4.115 3.358 5.869 3.358 2.447 0 4.358-1.605 4.358-4.478l-.004-.411c-.019-.672-.17-1.296-.714-2.62l-.248-.6c-1.065-2.478-5.993-12.768-7.538-15.664C18.053 3.539 17.24 3 16 3zm.01 10.316c-2.01 0-3.177 1.514-3.177 3.384 0 1.735 1.154 4.53 3.065 7.058 2.025-2.543 3.178-5.334 3.178-7.058 0-1.87-1.168-3.384-3.178-3.384z" />
                            </svg>
                        </div>

                        <div className={"sm: flex-col ml-4"}>
                            {/* Navigation Links */}
                            <div className="hidden md:flex items-center justify-center gap-6 mb-4">
                                <a href="#" className="font-medium text-sm">Stays</a>
                                <a href="#" className="font-medium text-sm">Experiences</a>
                                <a href="#" className="font-medium text-sm">Online Experiences</a>
                            </div>

                            {/* Search Bar */}
                            <div className="flex-1 max-w-[850px] mx-auto mt-2">
                                <div className="flex items-center border rounded-full shadow-sm hover:shadow-md transition cursor-pointer">
                                    <div className="px-6 py-2 border-r">
                                        <div className="font-medium text-sm">Where</div>
                                        <input
                                            type="text"
                                            placeholder="Search destinations"
                                            className="text-sm text-gray-600 outline-none bg-transparent w-[120px]"
                                        />
                                    </div>
                                    <div className="px-6 py-2 border-r">
                                        <div className="font-medium text-sm">Check in</div>
                                        <div className="text-sm text-gray-600">Add dates</div>
                                    </div>
                                    <div className="px-6 py-2 border-r">
                                        <div className="font-medium text-sm">Check out</div>
                                        <div className="text-sm text-gray-600">Add dates</div>
                                    </div>
                                    <div className="px-6 py-2">
                                        <div className="font-medium text-sm">Who</div>
                                        <div className="text-sm text-gray-600">Add guests</div>
                                    </div>
                                    <button className="bg-[#FF385C] p-2 rounded-full text-white mx-2">
                                        <Search className="w-4 h-4" />
                                    </button>
                                </div>
                            </div>
                        </div>


                        {/* Right Section */}
                        <div className="flex items-center gap-4">
                            <button className="hidden md:block text-sm font-medium hover:bg-gray-100 rounded-full px-4 py-3">
                                Airbnb your home
                            </button>
                            <button className="hover:bg-gray-100 rounded-full p-3">
                                <Globe className="w-5 h-5" />
                            </button>
                            <div className="flex items-center border rounded-full p-2 hover:shadow-md gap-3">
                                <Menu className="w-4 h-4" />
                                <div className="bg-gray-500 rounded-full">
                                    <User className="w-6 h-6 text-white" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Header;
