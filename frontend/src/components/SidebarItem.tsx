interface SidebarItemProps {
    title?: string
    selected?: boolean
    onClick?: () => void
}

const SidebarItem = ({title, selected, onClick}:SidebarItemProps) => {
    return (
        <div
            onClick={onClick}
            className={"flex py-4 px-10 hover:bg-[#204345] cursor-pointer transition-all " + (selected && "bg-[#204345]")}>
            {title}
        </div>
    )
}

export default SidebarItem;
