import {Wrap, WrapItem, Spinner, chakra} from '@chakra-ui/react'
import SidebarWithHeader from "./shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {errorNotification} from "./services/popup-notification.js";
import WordPackCard from "./components/word-pack/WordPackCard.jsx";
import {getAllWordPacks} from "./services/word-pack.js";

const WordPack = () => {

    const [allWordPacksDTO, setAllWordPacksDTO] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchAllWordPacksDTO = () => {
        setLoading(true);
        getAllWordPacks()
            .then(response => setAllWordPacksDTO(response.data.data.allWordPacksDTO))
            .catch(error => {
                setError((error.response.data.message))
                errorNotification(error.code, error.response.data.message)
            })
            .finally(() => setLoading(false))
    }

    useEffect(() => {
        fetchAllWordPacksDTO();
    }, [])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner thickness='4px' speed='0.65s' emptyColor='gray.200' color='blue.500' size='xl'/>
            </SidebarWithHeader>
        )
    }

    if (error) {
        return (
            <SidebarWithHeader>
                <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">
                    Ooops, there was an error
                </chakra.h1>
            </SidebarWithHeader>
        )
    }

    if (allWordPacksDTO.length <= 0) {
        return (
            <SidebarWithHeader>
                <chakra.h1 textAlign="center" fontSize="4xl" py={10} fontWeight="bold">
                    No Word Packs available
                </chakra.h1>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {allWordPacksDTO.map((wordPackDTO, index) => (
                    <WrapItem key={index}>
                        <WordPackCard wordPackDTO={wordPackDTO} fetchAllWordPacksDTO={fetchAllWordPacksDTO}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default WordPack;